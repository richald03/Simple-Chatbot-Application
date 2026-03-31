package com.example.simplechatbot;

import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String PLACEHOLDER_TYPING = "Typing...";
    private static final String MODEL_NAME = "gemini-2.5-flash";
    private static final String RESPONSE_STYLE_INSTRUCTION =
            "Answer accurately in clean plain text. Do not use markdown, asterisks, bold, bullet symbols, or decorative formatting.";
    private static final String PREFS_NAME = "chat_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_COLOR_THEME = "color_theme";
    private static final String STATE_MESSAGES = "state_messages";
    private static final String STATE_INPUT_TEXT = "state_input_text";
    private static final String STATE_SCROLL_POSITION = "state_scroll_position";

    private View rootLayout;
    private LinearLayout headerBar;
    private TextView titleText;
    private TextView subtitleText;
    private RecyclerView recyclerView;
    private LinearLayout composerBar;
    private EditText inputField;
    private ImageButton sendButton;
    private ImageButton themeToggleButton;
    private ImageButton paletteButton;

    private ChatAdapter adapter;
    private final List<ChatMessage> messageList = new ArrayList<>();

    private GenerativeModelFutures model;
    private ChatTheme currentTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(
                isDarkModeEnabled() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.rootLayout);
        headerBar = findViewById(R.id.headerBar);
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        recyclerView = findViewById(R.id.recyclerView);
        composerBar = findViewById(R.id.composerBar);
        inputField = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        themeToggleButton = findViewById(R.id.themeToggleButton);
        paletteButton = findViewById(R.id.paletteButton);

        adapter = new ChatAdapter(messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(false);
        applyColorTheme();

        initializeGemini();
        restoreChatState(savedInstanceState);
        updateThemeToggleIcon();
        updatePaletteButton();

        sendButton.setOnClickListener(v -> sendMessage());
        themeToggleButton.setOnClickListener(v -> toggleTheme());
        paletteButton.setOnClickListener(v -> showColorThemePicker());
        inputField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage();
                return true;
            }
            return false;
        });
    }

    private void initializeGemini() {
        if (TextUtils.isEmpty(BuildConfig.GEMINI_API_KEY)) {
            return;
        }

        GenerativeModel generativeModel = new GenerativeModel(
                MODEL_NAME,
                BuildConfig.GEMINI_API_KEY
        );

        model = GenerativeModelFutures.from(generativeModel);
    }

    private void sendMessage() {
        String text = inputField.getText().toString().trim();

        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, R.string.error_empty_message, Toast.LENGTH_SHORT).show();
            return;
        }

        addUserMessage(text);
        inputField.setText("");

        if (model == null) {
            addBotMessage(getString(R.string.error_missing_api_key));
            return;
        }

        setSendingState(true);
        addBotMessage(PLACEHOLDER_TYPING);
        int typingIndex = messageList.size() - 1;

        callGemini(text, typingIndex);
    }

    private void callGemini(String prompt, int typingIndex) {
        try {
            Content content = new Content.Builder()
                    .addText(RESPONSE_STYLE_INSTRUCTION + "\n\nUser question: " + prompt)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    runOnUiThread(() -> {
                        removeTypingMessage(typingIndex);
                        setSendingState(false);

                        String botResponse = result.getText();
                        if (TextUtils.isEmpty(botResponse)) {
                            botResponse = getString(R.string.empty_ai_response);
                        }

                        addBotMessage(cleanAiResponse(botResponse));
                    });
                }

                @Override
                public void onFailure(@NonNull Throwable t) {
                    Log.e(TAG, "Gemini API error", t);

                    runOnUiThread(() -> {
                        removeTypingMessage(typingIndex);
                        setSendingState(false);
                        addBotMessage(getString(R.string.error_prefix, resolveErrorMessage(t)));
                    });
                }
            }, getMainExecutor());
        } catch (Exception e) {
            Log.e(TAG, "Unexpected Gemini error", e);
            removeTypingMessage(typingIndex);
            setSendingState(false);
            addBotMessage(getString(R.string.error_prefix, resolveErrorMessage(e)));
        }
    }

    private void setSendingState(boolean isSending) {
        sendButton.setEnabled(!isSending);
        inputField.setEnabled(!isSending);
        inputField.setHint(isSending
                ? getString(R.string.hint_waiting_response)
                : getString(R.string.hint_type_message));
    }

    private void removeTypingMessage(int typingIndex) {
        if (typingIndex >= 0 && typingIndex < messageList.size()) {
            messageList.remove(typingIndex);
            adapter.notifyItemRemoved(typingIndex);
        }
    }

    private void addUserMessage(String text) {
        messageList.add(new ChatMessage(text, true, getCurrentTime()));
        notifyMessageAdded();
    }

    private void addBotMessage(String text) {
        messageList.add(new ChatMessage(text, false, getCurrentTime()));
        notifyMessageAdded();
    }

    private void notifyMessageAdded() {
        int position = messageList.size() - 1;
        adapter.notifyItemInserted(position);
        smoothScrollToPosition(position);
    }

    private void restoreChatState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            Object savedMessages = savedInstanceState.getSerializable(STATE_MESSAGES);
            if (savedMessages instanceof ArrayList<?>) {
                for (Object item : (ArrayList<?>) savedMessages) {
                    if (item instanceof ChatMessage) {
                        messageList.add((ChatMessage) item);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            inputField.setText(savedInstanceState.getString(STATE_INPUT_TEXT, ""));

            int scrollPosition = savedInstanceState.getInt(STATE_SCROLL_POSITION, -1);
            if (scrollPosition >= 0) {
                recyclerView.post(() -> recyclerView.scrollToPosition(scrollPosition));
            }
        }

        if (messageList.isEmpty()) {
            if (TextUtils.isEmpty(BuildConfig.GEMINI_API_KEY)) {
                addBotMessage(getString(R.string.error_missing_api_key));
            } else {
                addBotMessage(getString(R.string.welcome_message));
            }
        }
    }

    private void toggleTheme() {
        boolean enableDarkMode = !isDarkModeEnabled();
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putBoolean(KEY_DARK_MODE, enableDarkMode)
                .apply();

        AppCompatDelegate.setDefaultNightMode(
                enableDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        updateThemeToggleIcon();
    }

    private void showColorThemePicker() {
        final String[] themeKeys = {
                ChatTheme.THEME_BLUE,
                ChatTheme.THEME_GREEN,
                ChatTheme.THEME_ROSE
        };
        final String[] themeLabels = {
                getString(R.string.theme_blue),
                getString(R.string.theme_green),
                getString(R.string.theme_rose)
        };

        int selectedIndex = 0;
        String currentThemeKey = getSelectedColorTheme();
        for (int i = 0; i < themeKeys.length; i++) {
            if (themeKeys[i].equals(currentThemeKey)) {
                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.choose_chat_theme)
                .setSingleChoiceItems(themeLabels, selectedIndex, (dialog, which) -> {
                    saveSelectedColorTheme(themeKeys[which]);
                    applyColorTheme();
                    dialog.dismiss();
                })
                .setNegativeButton(android.R.string.cancel, null)
                .show();
    }

    private boolean isDarkModeEnabled() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getBoolean(KEY_DARK_MODE, false);
    }

    private String getSelectedColorTheme() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_COLOR_THEME, ChatTheme.THEME_BLUE);
    }

    private void saveSelectedColorTheme(String themeKey) {
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                .edit()
                .putString(KEY_COLOR_THEME, themeKey)
                .apply();
    }

    private void updateThemeToggleIcon() {
        if (themeToggleButton == null) {
            return;
        }

        boolean isDarkMode = isDarkModeActive();

        themeToggleButton.setImageResource(
                isDarkMode ? R.drawable.ic_light_mode : R.drawable.ic_dark_mode
        );
        themeToggleButton.setContentDescription(
                getString(isDarkMode ? R.string.switch_to_light_mode : R.string.switch_to_dark_mode)
        );
    }

    private void updatePaletteButton() {
        if (paletteButton == null) {
            return;
        }
        paletteButton.setContentDescription(getString(R.string.choose_chat_theme));
    }

    private void smoothScrollToPosition(int position) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (!(layoutManager instanceof LinearLayoutManager)) {
            recyclerView.smoothScrollToPosition(position);
            return;
        }

        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override
            protected float calculateSpeedPerPixel(android.util.DisplayMetrics displayMetrics) {
                return 85f / displayMetrics.densityDpi;
            }
        };
        smoothScroller.setTargetPosition(position);
        layoutManager.startSmoothScroll(smoothScroller);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
    }

    private String resolveErrorMessage(Throwable throwable) {
        String rawMessage = throwable.getMessage() == null ? "" : throwable.getMessage();
        String lowerMessage = rawMessage.toLowerCase(Locale.US);

        if (rawMessage.contains("404")) {
            return getString(R.string.error_model_not_found);
        }
        if (rawMessage.contains("429")
                || lowerMessage.contains("quota")
                || lowerMessage.contains("rate limit")
                || lowerMessage.contains("resource exhausted")
                || lowerMessage.contains("too many requests")) {
            return getString(R.string.error_quota_limit);
        }
        if (rawMessage.contains("401") || rawMessage.contains("403")) {
            return getString(R.string.error_authentication);
        }
        if (lowerMessage.contains("network") || lowerMessage.contains("unable to resolve host")) {
            return getString(R.string.error_network);
        }
        if (!TextUtils.isEmpty(rawMessage)) {
            return rawMessage;
        }
        return getString(R.string.error_generic);
    }

    private String cleanAiResponse(String response) {
        String cleaned = response == null ? "" : response.trim();
        cleaned = cleaned.replace("\r\n", "\n");
        cleaned = cleaned.replaceAll("[\\t ]+\n", "\n");
        cleaned = cleaned.replaceAll("\n{3,}", "\n\n");
        cleaned = cleaned.replaceAll("[ ]{2,}", " ");
        cleaned = cleaned.replace("*", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*[-•]+\\s*", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*#+\\s*", "");
        cleaned = cleaned.replaceAll("(?m)^\\s*\\d+\\.\\s*", "");
        cleaned = cleaned.replaceAll("\\n +", "\n");
        cleaned = cleaned.trim();

        if (TextUtils.isEmpty(cleaned)) {
            return getString(R.string.empty_ai_response);
        }
        return cleaned;
    }

    private void applyColorTheme() {
        currentTheme = ChatTheme.from(getSelectedColorTheme(), isDarkModeActive());
        adapter.setChatTheme(currentTheme);

        rootLayout.setBackgroundColor(currentTheme.background);
        titleText.setTextColor(0xFFFFFFFF);
        subtitleText.setTextColor(0xE6FFFFFF);
        inputField.setTextColor(currentTheme.textPrimary);
        inputField.setHintTextColor(currentTheme.textSecondary);

        tintGradientBackground(headerBar, currentTheme.primaryDark, currentTheme.accent, 0);
        tintSolidBackground(composerBar, currentTheme.surface, currentTheme.divider);
        tintSolidBackground(inputField, currentTheme.surface, currentTheme.divider);
        tintGradientBackground(sendButton, currentTheme.primary, currentTheme.accent, 0);

        int toggleFill = applyAlpha(currentTheme.surface, isDarkModeActive() ? 0.14f : 0.18f);
        tintSolidBackground(themeToggleButton, toggleFill, applyAlpha(0xFFFFFFFF, 0.18f));
        tintSolidBackground(paletteButton, toggleFill, applyAlpha(0xFFFFFFFF, 0.18f));

        getWindow().setStatusBarColor(currentTheme.primaryDark);
    }

    private boolean isDarkModeActive() {
        return (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
    }

    private void tintGradientBackground(View view, int startColor, int endColor, int strokeColor) {
        Drawable background = view.getBackground().mutate();
        if (background instanceof GradientDrawable) {
            GradientDrawable shape = (GradientDrawable) background;
            shape.setColors(new int[]{startColor, endColor});
            if (strokeColor != 0) {
                shape.setStroke(dpToPx(1), strokeColor);
            }
        }
        view.setBackground(background);
    }

    private void tintSolidBackground(View view, int fillColor, int strokeColor) {
        Drawable background = view.getBackground().mutate();
        if (background instanceof GradientDrawable) {
            GradientDrawable shape = (GradientDrawable) background;
            shape.setColor(fillColor);
            if (strokeColor != 0) {
                shape.setStroke(dpToPx(1), strokeColor);
            }
        }
        view.setBackground(background);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private int applyAlpha(int color, float alphaFraction) {
        int alpha = Math.round(255 * alphaFraction);
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE_MESSAGES, new ArrayList<>(messageList));
        outState.putString(STATE_INPUT_TEXT, inputField.getText().toString());

        int scrollPosition = -1;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            scrollPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
        outState.putInt(STATE_SCROLL_POSITION, scrollPosition);
    }
}
