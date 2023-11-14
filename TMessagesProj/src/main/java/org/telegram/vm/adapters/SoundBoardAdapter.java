package org.telegram.vm.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import org.telegram.messenger.R;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.vm.adapters.models.UISound;
import org.telegram.vm.animations.ProgressBarAnimation;
import org.telegram.vm.ui.LineBarVisualizer;

import java.util.ArrayList;
import java.util.function.Function;

public class SoundBoardAdapter extends RecyclerView.Adapter<SoundBoardAdapter.SoundBoardViewHolder> {

    private final Context context;

    private MediaPlayer mediaPlayer;
    private ArrayList<UISound> sounds;
    private ArrayList<UISound> currentlySendingSounds;
    private RecyclerListView.OnItemClickListener onItemClickListener;
    private RecyclerListView.OnItemLongClickListener onItemLongClickListener;
    private Function<Void,Void> onStopClickedListener;
    private Function<UISound,Void> onItemLongClickCancelListener;
    private UISound previewingSound = null;
    private UISound sendingSoundInACall = null;
    private final boolean isBottomSheet;
    private boolean progressAnimationCleared;

    private SoundBoardViewHolder currentLongPressedHolder;
    private SoundBoardViewHolder currentClickedHolder;

    public SoundBoardAdapter(Context context, ArrayList<UISound> sounds, ArrayList<UISound> currentlySendingSounds, boolean isBottomSheet) {
        this.context = context;
        this.sounds = sounds;
        this.currentlySendingSounds = currentlySendingSounds;
        this.isBottomSheet = isBottomSheet;
    }

    public boolean isBottomSheet() {
        return isBottomSheet;
    }

    @Override
    public int getItemCount() {
        return sounds.size();
    }

    @Override
    public SoundBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.soundboard_list_item, parent, false);
        return new SoundBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SoundBoardViewHolder holder, int position) {
        UISound sound = sounds.get(position);
        holder.soundNameTv.setText(sound.name);
        holder.soundAuthorTv.setText(sound.author);
        holder.soundAuthorTv.setText(sound.author);

        Picasso.get().load(sound.imageUrl).centerCrop().fit()
                .placeholder(R.color.vm_grey_manatee).into(holder.soundImg);

        holder.itemView.setOnClickListener(view -> {
            if (previewingSound == null && currentlySendingSounds.isEmpty() && sendingSoundInACall == null) {
                if (isBottomSheet) {
                    sendingSoundInACall = sound;
                    if (currentClickedHolder != null) {
                        if (currentClickedHolder.soundProgress.getAnimation() != null){
                            currentClickedHolder.soundProgress.getAnimation().setAnimationListener(null);
                            currentClickedHolder.soundProgress.getAnimation().cancel();
                        }

                        currentClickedHolder.soundProgress.clearAnimation();
                        currentClickedHolder.stopBtn.setVisibility(View.INVISIBLE);
                        currentClickedHolder.soundProgress.setVisibility(View.INVISIBLE);
                        currentClickedHolder.loader.setVisibility(View.INVISIBLE);

                        onStopClickedListener.apply(null);
                    }
                    currentClickedHolder = holder;
                    currentClickedHolder.loader.setVisibility(View.VISIBLE);
                    currentClickedHolder.soundProgress.setProgress(0);
                    currentClickedHolder.soundProgress.setVisibility(View.VISIBLE);
                    currentClickedHolder.soundImg.setAlpha(0.5f);

                    notifyItemRangeChanged(0, position);
                    notifyItemRangeChanged(position+1, sounds.size());
                }

                onItemClickListener.onItemClick(view, position);

                if (!isBottomSheet){
                    notifyDataSetChanged();
                }
            }
        });

        holder.stopBtn.setOnClickListener(view -> {
            holder.soundProgress.clearAnimation();
            holder.stopBtn.setVisibility(View.INVISIBLE);
            holder.soundProgress.setVisibility(View.INVISIBLE);
            if (currentClickedHolder != null && currentClickedHolder.loader != null) {
                currentClickedHolder.loader.setVisibility(View.INVISIBLE);
            }
            sendingSoundInACall = null;

            onStopClickedListener.apply(null);
            soundStoppedPlaying(null);
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (previewingSound == null && currentlySendingSounds.isEmpty() && sendingSoundInACall == null) {
                currentLongPressedHolder = holder;
                previewingSound = sound;
                onItemLongClickListener.onItemClick(view, position);
                notifyItemRangeChanged(0, position);
                notifyItemRangeChanged(position + 1, sounds.size());
            }
            return false;
        });

        holder.itemView.setOnTouchListener((view, event) -> processOnActionUpAfterLongPress(event));

        if (currentlySendingSounds.contains(sound)) {
            holder.loader.setVisibility(View.VISIBLE);
        } else {
            holder.loader.setVisibility(View.INVISIBLE);
        }

        if (previewingSound == sound) {
            setSoundVisualizer(mediaPlayer, holder.soundVisualizer);
        } else {
            holder.soundVisualizer.setVisibility(View.INVISIBLE);
        }

        if (isBottomSheet) {
            setBottomSheetView(holder, sound);
            holder.soundGradient.setVisibility(View.GONE);

        }else {
            if (currentlySendingSounds.contains(sound) || previewingSound == sound) {
                holder.itemView.setAlpha(1);
            } else {
                if (!currentlySendingSounds.isEmpty() || previewingSound != null) {
                    holder.itemView.setAlpha(0.5f);
                } else {
                    holder.itemView.setAlpha(1);
                }
            }
        }
    }

    private void setBottomSheetView(SoundBoardViewHolder holder, UISound sound) {
        holder.itemView.setAlpha(1F);
        holder.soundImg.setAlpha(1F);
        if (sendingSoundInACall != null) {
            if (sendingSoundInACall == sound) {
                holder.itemView.setAlpha(1);
                holder.soundImg.setAlpha(0.5F);
            } else {
                holder.soundImg.setAlpha(1F);
                holder.itemView.setAlpha(0.5F);
            }
        } else {
            if (previewingSound == null) {
                holder.itemView.setAlpha(1);
            } else {
                holder.itemView.setAlpha(previewingSound == sound ? 1.0F : 0.5F);
            }
        }

        holder.soundCardView.setRadius(300);
        holder.soundVmLogo.setVisibility(View.GONE);
    }

    public void sendParentEventToCurrentSelectedHolder(MotionEvent event){
        if (currentLongPressedHolder != null){
            processOnActionUpAfterLongPress(event);
        }
    }

    private boolean processOnActionUpAfterLongPress(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_UP) {
            currentLongPressedHolder = null;
            if (previewingSound == null) return false;
            onItemLongClickCancelListener.apply(previewingSound);
        }

        return false;
    }

    private void setSoundVisualizer(MediaPlayer mediaPlayer, LineBarVisualizer soundVisualizer) {
        try {
            soundVisualizer.setVisibility(View.VISIBLE);
            soundVisualizer.setDensity(18);
            soundVisualizer.setColor(ContextCompat.getColor(context, R.color.vm_white));
            soundVisualizer.setPlayer(mediaPlayer.getAudioSessionId());
        } catch (Exception e) {
            //Nothing to do here. The user didn't granted RECORD permissions, so we cannot access to the audio session
            soundVisualizer.setVisibility(View.GONE);
        }
    }

    public void soundStoppedPlaying(RecyclerView.ViewHolder itemViewHolder) {
        previewingSound = null;
        if (itemViewHolder != null) {
            ((SoundBoardViewHolder) itemViewHolder).soundVisualizer.release();
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(RecyclerListView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(RecyclerListView.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnStopClickedListener(Function<Void,Void> onStopClickedListener) {
        this.onStopClickedListener = onStopClickedListener;
    }

    public void setOnItemLongClickCancelListener(Function<UISound,Void> onItemLongClickCancelListener) {
        this.onItemLongClickCancelListener = onItemLongClickCancelListener;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void hideCurrentItemLoader(){
        if (currentClickedHolder != null) currentClickedHolder.loader.setVisibility(View.INVISIBLE);
    }

    public void setCurrentlyPlayingAudioDuration(int duration) {
        if (currentClickedHolder != null) {
            currentClickedHolder.stopBtn.setVisibility(View.VISIBLE);
            currentClickedHolder.loader.setVisibility(View.INVISIBLE);

            ProgressBarAnimation animation = new ProgressBarAnimation(currentClickedHolder.soundProgress, 0, 100);
            animation.setDuration(duration);



            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    currentClickedHolder.stopBtn.setVisibility(View.INVISIBLE);
                    currentClickedHolder.soundProgress.setVisibility(View.INVISIBLE);

                    sendingSoundInACall = null;
                    notifyItemRangeChanged(0, sounds.size());

                    currentClickedHolder = null;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            currentClickedHolder.soundProgress.startAnimation(animation);
        }
    }

    static class SoundBoardViewHolder extends RecyclerView.ViewHolder {

        public final TextView soundNameTv, soundAuthorTv;
        public final ImageView soundImg, soundVmLogo, soundGradient;
        public final FrameLayout stopBtn;
        public final LineBarVisualizer soundVisualizer;
        public final ProgressBar loader, soundProgress;
        public final CardView soundCardView;

        public SoundBoardViewHolder(View view) {
            super(view);

            soundNameTv = view.findViewById(R.id.sound_name_tv);
            soundAuthorTv = view.findViewById(R.id.sound_author_tv);
            soundImg = view.findViewById(R.id.sound_img);
            soundVmLogo = view.findViewById(R.id.sound_vm_logo);
            soundVisualizer = view.findViewById(R.id.sound_visualizer);
            loader = view.findViewById(R.id.sound_loader);
            soundCardView = view.findViewById(R.id.sound_cardview);
            stopBtn = view.findViewById(R.id.sound_stop_btn);
            soundProgress = view.findViewById(R.id.sound_progress);
            soundGradient = view.findViewById(R.id.sound_gradient_top);
        }
    }
}
