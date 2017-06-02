package com.example.contactbook;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by me on 31.05.2017.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    List<Contact> contacts;

    public class ContactViewHolder extends RecyclerView.ViewHolder{

        public TextView name, number;
        public ImageView avatar;
        public ContactViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            number = (TextView) itemView.findViewById(R.id.number);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }

    public ContactAdapter(List<Contact> contacts) {
        this.contacts = contacts;
    }

    @Override
    public ContactViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ContactViewHolder holder, int position) {

        final Contact contact = contacts.get(position);
        holder.avatar.setImageURI(Uri.parse(contact.getPhoto()));
        holder.name.setText(contact.getName() + " " + contact.getLastName());
        holder.number.setText(contact.getNumber());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewContactActivity.class);
                intent.putExtra("name", contact.getName());
                intent.putExtra("last_name", contact.getLastName());
                intent.putExtra("number", contact.getNumber());
                intent.putExtra("email", contact.getEmail());
                intent.putExtra("_id", contact.getId());
                intent.putExtra("image", contact.getPhoto());
                v.getContext().startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

}
