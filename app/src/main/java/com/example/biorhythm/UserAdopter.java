package com.example.biorhythm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.data.Entry;

import java.util.Calendar;
import java.util.List;

public class UserAdopter extends RecyclerView.Adapter<UserAdopter.ViewHolder>{

    List<User> users;
    Context context;
    Calendar calendar;

    UserAdopter(List<User> users){
        this.users = users;
    }

    @NonNull
    @Override
    public UserAdopter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
        View view = inflater.inflate(R.layout.user_listview, parent, false) ;
        calendar = BiorhythmActivity.getDay(context, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdopter.ViewHolder holder, int position) {
        User user = users.get(position);
        String sum = String.format("\t 총합 %.2f", getSum(user.getBirthday()));
        holder.tv_birth.setText(user.getName() + sum + "\n"+ String.format("Birthday %d. %d. %d", user.birthday.get(Calendar.YEAR), user.birthday.get(Calendar.MONTH)+1, user.birthday.get(Calendar.DAY_OF_MONTH)));
        holder.btn_profile.setTag(position);
        holder.btn_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag();
                User user = users.get(pos);
                Intent intent = new Intent(context, UserActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("index", pos);
                context.startActivity(intent);
            }
        });
        if(user.getImage() != null){
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                holder.profile.setImageURI(user.getImage());
                holder.profile.setColorFilter(null);
            }else {
                holder.profile.setImageResource(R.drawable.profile);
                holder.profile.setColorFilter(user.getColor());
            }
        }else {
            holder.profile.setImageResource(R.drawable.profile);
            holder.profile.setColorFilter(user.getColor());
        }


    }

    public void refresh(){
        calendar = BiorhythmActivity.getDay(context, false);
        notifyDataSetChanged();
    }

    public float getSum(Calendar birth){
        Calendar current = BiorhythmActivity.getDay(context, false);
        float phySum = 0;
        float emoSum = 0;
        float intellSum = 0;
        float lastPhy = 0;
        float lastEmo = 0;
        float lastIntell = 0;

        for(int i = 0; i < 5; i++){
            float phy = getPhysical(birth, current);
            float emo = getEmotional(birth, current);
            float intell = getIntellectual(birth, current);

            if(i > 0){
                phySum +=  Math.abs(phy - lastPhy);
                emoSum +=  Math.abs(emo - lastEmo);
                intellSum +=  Math.abs(intell - lastIntell);
            }
            lastPhy = phy;
            lastEmo = emo;
            lastIntell = intell;
            current.add(Calendar.DAY_OF_MONTH, 1);
        }

        return phySum + emoSum + intellSum;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_birth;
        ImageView profile;
        ImageView btn_profile;

        ViewHolder(View itemView) {
            super(itemView);
            tv_birth = itemView.findViewById(R.id.tv_birth);
            profile = itemView.findViewById(R.id.profile);
            btn_profile = itemView.findViewById(R.id.btn_profile);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, BiorhythmActivity.class);
                        intent.putExtra("user", users.get(pos));
                        context.startActivity(intent);
                    }
                }
            });
        }
    }


    public float getPhysical(Calendar birth, Calendar current){
        long diff = (current.getTimeInMillis() - birth.getTimeInMillis()) / 1000;
        long day = diff / (24*60*60);
        return (float) (Math.round(Math.sin((2*Math.PI*day)/23f)*100)/100.0);
    }
    public float getEmotional(Calendar birth, Calendar current){
        long diff = (current.getTimeInMillis() - birth.getTimeInMillis()) / 1000;
        long day = diff / (24*60*60);
        return (float) (Math.round(Math.sin((2*Math.PI*day)/28f)*100)/100.0);
    }
    public float getIntellectual(Calendar birth, Calendar current){
        long diff = (current.getTimeInMillis() - birth.getTimeInMillis()) / 1000;
        long day = diff / (24*60*60);
        return (float) (Math.round(Math.sin((2*Math.PI*day)/33f)*100)/100.0);
    }
}
