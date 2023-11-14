package org.telegram.vm.sounds.suggestions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.telegram.messenger.R;

import java.util.function.Function;


public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.SuggestionViewHolder>{

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        public final TextView suggestion;
        public final View suggestionRow;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);

            suggestionRow = itemView;
            suggestion = itemView.findViewById(R.id.suggestionText);
        }
    }

    private String[] dataSet;
    private Function<String,Void> onSuggestionClicked;

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_suggestion_item_view, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        holder.suggestion.setText(dataSet[position]);

        holder.suggestionRow.setOnClickListener(v -> onSuggestionClicked.apply(dataSet[position]));
    }

    @Override
    public int getItemCount() {
        return (dataSet != null) ? dataSet.length : 0;
    }

    public void setOnSuggestionClicked(Function<String,Void> onSuggestionClicked) {
        this.onSuggestionClicked = onSuggestionClicked;
    }

    public void setDataSet(String[] dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

}
