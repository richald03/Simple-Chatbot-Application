package com.example.simplechatbot;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    private final List<ChatMessage> messages;
    private ChatTheme chatTheme = ChatTheme.from(ChatTheme.THEME_BLUE, false);

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void setChatTheme(ChatTheme chatTheme) {
        this.chatTheme = chatTheme;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_user_message, parent, false);
            return new UserViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bot_message, parent, false);
        return new BotViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        if (holder instanceof UserViewHolder) {
            UserViewHolder userHolder = (UserViewHolder) holder;
            userHolder.tvSender.setText(R.string.user_name);
            userHolder.tvSender.setTextColor(chatTheme.textSecondary);
            userHolder.tvMessage.setText(message.getText());
            userHolder.tvMessage.setTextColor(chatTheme.userText);
            styleGradientBubble(userHolder.tvMessage, chatTheme.userBubbleStart, chatTheme.userBubbleEnd, chatTheme.userBubbleStroke);
            userHolder.tvTime.setText(message.getTime());
            userHolder.tvTime.setTextColor(chatTheme.timeUserText);
            tintSolidBackground(userHolder.tvTime, chatTheme.timeUserBg, 0);
            userHolder.tvAvatar.setTextColor(chatTheme.userAvatarText);
            tintSolidBackground(userHolder.tvAvatar, chatTheme.userAvatarFill, 0);
        } else if (holder instanceof BotViewHolder) {
            BotViewHolder botHolder = (BotViewHolder) holder;
            botHolder.tvSender.setText(R.string.bot_name);
            botHolder.tvSender.setTextColor(chatTheme.textSecondary);
            botHolder.tvMessage.setText(message.getText());
            botHolder.tvMessage.setTextColor(chatTheme.botText);
            tintSolidBackground(botHolder.tvMessage, chatTheme.botBubble, chatTheme.botBubbleStroke);
            botHolder.tvTime.setText(message.getTime());
            botHolder.tvTime.setTextColor(chatTheme.timeBotText);
            tintSolidBackground(botHolder.tvTime, chatTheme.timeBotBg, 0);
            botHolder.tvAvatar.setTextColor(chatTheme.botAvatarText);
            tintSolidBackground(botHolder.tvAvatar, chatTheme.botAvatarFill, 0);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSender;
        final TextView tvMessage;
        final TextView tvTime;
        final TextView tvAvatar;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSender;
        final TextView tvMessage;
        final TextView tvTime;
        final TextView tvAvatar;

        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender = itemView.findViewById(R.id.tvSender);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvAvatar = itemView.findViewById(R.id.tvAvatar);
        }
    }

    private void styleGradientBubble(TextView textView, int startColor, int endColor, int strokeColor) {
        Drawable background = textView.getBackground().mutate();
        if (background instanceof GradientDrawable) {
            GradientDrawable shape = (GradientDrawable) background;
            shape.setColors(new int[]{startColor, endColor});
            shape.setStroke(dpToPx(textView, 1), strokeColor);
        }
        textView.setBackground(background);
    }

    private void tintSolidBackground(TextView textView, int fillColor, int strokeColor) {
        Drawable background = textView.getBackground().mutate();
        if (background instanceof GradientDrawable) {
            GradientDrawable shape = (GradientDrawable) background;
            shape.setColor(fillColor);
            if (strokeColor != 0) {
                shape.setStroke(dpToPx(textView, 1), strokeColor);
            }
        }
        textView.setBackground(background);
    }

    private int dpToPx(TextView textView, int dp) {
        float density = textView.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
