package com.friox.kazehikarufinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ListObject> mList;
    private OnItemClickListener mListener = null;

    private static final int VIEWTYPE_PARENT = 0;
    private static final int VIEWTYPE_DIR = 1;
    private static final int VIEWTYPE_VIDEO = 2;

    private int goodColor;
    private int badColor;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }

    ItemAdapter(ArrayList<ListObject> list, Context context) {
        mList = list;
        goodColor = ContextCompat.getColor(context, R.color.progressGood);
        badColor = ContextCompat.getColor(context, R.color.progressBad);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (viewType == VIEWTYPE_PARENT) {
            view = inflater.inflate(R.layout.listview_item_parent, parent, false);
            return new ParentViewHolder(view);
        } else if (viewType == VIEWTYPE_DIR) {
            view = inflater.inflate(R.layout.listview_item_dir, parent, false);
            return new DirViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.listview_item_video, parent, false);
            return new VideoViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ParentViewHolder) {
            // ParentViewHolder Binding Action
        } else if (holder instanceof DirViewHolder) {
            ((DirViewHolder)holder).title.setText(mList.get(position).getTitle());
        } else {
            ListObject listObject = mList.get(position);
            VideoViewHolder mHolder = (VideoViewHolder)holder;
            mHolder.title.setText(listObject.getTitle());
            mHolder.size.setText(listObject.getSize());
            mHolder.format.setText(listObject.getFileExtn());
            mHolder.episode.setText(listObject.getEpisode());

            if (listObject.getSubStatus() == 1) mHolder.subStatus.setTextColor(goodColor);
            else mHolder.subStatus.setTextColor(badColor);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getViewType();
    }

    public class ParentViewHolder extends RecyclerView.ViewHolder {
        ParentViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }

    public class DirViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        DirViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
            title = itemView.findViewById(R.id.title_textview);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView size;
        TextView subStatus;
        TextView format;
        TextView episode;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_textview);
            size = itemView.findViewById(R.id.size_textview);
            subStatus = itemView.findViewById(R.id.sub_status);
            format = itemView.findViewById(R.id.format_textview);
            episode = itemView.findViewById(R.id.episode_textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        if (mListener != null) {
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}
