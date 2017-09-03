package com.tstudioz.fax.fme.adapters;



import android.graphics.Typeface;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tstudioz.fax.fme.R;
import com.tstudioz.fax.fme.database.Materijal;


import io.realm.RealmChangeListener;
import io.realm.RealmList;



public class MaterialsAdapter extends RecyclerView.Adapter<MaterialsAdapter.MaterialViewHolder> implements RealmChangeListener {
    private RealmList<Materijal> materials;
    Typeface regulartf;


    public MaterialsAdapter(RealmList<Materijal> material) {
        this.materials = material;
        materials.addChangeListener(this);
    }

    @Override
    public MaterialViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.materijal_item, parent, false);
        return new MaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MaterialViewHolder holder, int position) {

        Materijal materijal = materials.get(position);
        holder.name.setText(materijal.getImeMtarijala());
        holder.name.setTypeface(regulartf);


        if (materijal.getIkonaUrl()!=null) {
            switch (materijal.getIkonaUrl()) {
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fpdf&rev=305":
                    holder.icon.setImageResource(R.drawable.pdf);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fword&rev=305":
                    holder.icon.setImageResource(R.drawable.word);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fdocm&rev=305":
                    holder.icon.setImageResource(R.drawable.word);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fdocx&rev=305":
                    holder.icon.setImageResource(R.drawable.word);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fpptx&rev=305":
                    holder.icon.setImageResource(R.drawable.ppt);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fxlsx&rev=305":
                    holder.icon.setImageResource(R.drawable.excel);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=icon&rev=305&component=folder":
                    holder.icon.setImageResource(R.drawable.folder);
                    holder.download.setImageResource(R.drawable.open_in_browser);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Ftext&rev=305":
                    holder.icon.setImageResource(R.drawable.txt);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=icon&rev=305&component=choice":
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=icon&rev=305&component=quiz":
                    holder.icon.setImageResource(R.drawable.quiz);
                    holder.download.setImageResource(R.drawable.open_in_browser);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=icon&rev=305&component=assignment":
                    holder.icon.setImageResource(R.drawable.assign);
                    holder.download.setImageResource(R.drawable.open_in_browser);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fhtml&rev=305":
                    holder.icon.setImageResource(R.drawable.link);
                    holder.download.setImageResource(R.drawable.open_in_browser);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=icon&rev=305&component=page":
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fweb&rev=305":
                    holder.icon.setImageResource(R.drawable.link);
                    break;
                case "https://elearning.fesb.unist.hr/theme/image.php?theme=fesb_metro&image=f%2Fzip&rev=305":
                    holder.icon.setImageResource(R.drawable.archive);
                    holder.download.setVisibility(View.VISIBLE);
                    break;
                default: holder.icon.setImageResource(R.drawable.unknown);
                    break;
            }



        }



        }


    @Override
    public int getItemCount() {
        return materials.size();
    }

    public class MaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        ImageView icon, download;


        public MaterialViewHolder(final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.mat_text);
            regulartf = Typeface.createFromAsset(itemView.getContext().getAssets(), "fonts/OpenSans-Light.ttf");
            name.setTypeface(regulartf);

            icon = (ImageView)itemView.findViewById(R.id.mat_src);
            download = (ImageView)itemView.findViewById(R.id.mat_dl);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

        @Override
        public void onChange(Object element) {
            notifyDataSetChanged();
        }


}


