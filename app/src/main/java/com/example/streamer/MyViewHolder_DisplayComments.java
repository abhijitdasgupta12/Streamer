package com.example.streamer;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyViewHolder_DisplayComments extends RecyclerView.ViewHolder
{
    CircleImageView circleImageView;
    TextView commentUserName, commentDateTime, commentMessage;
    public MyViewHolder_DisplayComments(@NonNull View itemView)
    {
        super(itemView);
        circleImageView= itemView.findViewById(R.id.commentUserImage);
        commentUserName= itemView.findViewById(R.id.commentUserNameTextView);
        commentDateTime= itemView.findViewById(R.id.commentDateTimeTextView);
        commentMessage= itemView.findViewById(R.id.commentMessageTextView);
    }
}
