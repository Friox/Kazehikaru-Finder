package com.friox.kazehikarufinder;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        if (viewType == VIEWTYPE_VIDEO) {
            view = inflater.inflate(R.layout.listview_item_video, parent, false);
            return new VideoViewHolder(view);
        } else {
            view = inflater.inflate(R.layout.listview_item_single, parent, false);
            return new SingleViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof SingleViewHolder) {
            ListObject listObject = mList.get(position);
            int viewType = listObject.getViewType();
            SingleViewHolder mHolder = ((SingleViewHolder)holder);
            if (viewType == VIEWTYPE_PARENT) {
                mHolder.title.setTypeface(Typeface.DEFAULT_BOLD);
            } else {
                // folder
                mHolder.title.setText(listObject.getTitle());
                mHolder.icon.setImageResource(R.drawable.ic_folder_black_24dp);
            }
        } else {
            ListObject listObject = mList.get(position);
            VideoViewHolder mHolder = (VideoViewHolder)holder;
            mHolder.title.setText(listObject.getTitle());
            mHolder.size.setText(listObject.getSize());
            mHolder.format.setText(listObject.getFileExtn());
            mHolder.episode.setText(listObject.getEpisode());

            if (listObject.getSubStatus() == 1) mHolder.subStatus.setTextColor(goodColor);
            else mHolder.subStatus.setTextColor(badColor);
            if (listObject.getFileExtn().equals("part")) mHolder.icon.setImageResource(R.drawable.ic_file_download_black_24dp);
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

    public class SingleViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;
        TextView title;

        SingleViewHolder(@NonNull View itemView) {
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
            icon = itemView.findViewById(R.id.list_icon);
            title = itemView.findViewById(R.id.list_title);
        }
    }

    public class VideoViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView size;
        TextView subStatus;
        TextView format;
        TextView episode;
        ImageView icon;

        VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_textview);
            size = itemView.findViewById(R.id.size_textview);
            subStatus = itemView.findViewById(R.id.sub_status);
            format = itemView.findViewById(R.id.format_textview);
            episode = itemView.findViewById(R.id.episode_textview);
            icon = itemView.findViewById(R.id.movie_image);
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
