package io.archylex.meterreadingsinfo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class MeterServiceAdapter extends ArrayAdapter<MeterService> {
    private final Activity context;
    private List<MeterService> services;

    public MeterServiceAdapter(Activity context, List<MeterService> services) {
        super(context, R.layout.service_list_cell, services);
        this.context = context;
        this.services = services;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.service_list_cell, null,true);

        TextView titleText = (TextView) rowView.findViewById(R.id.org_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.org_icon);
        TextView subtitleText = (TextView) rowView.findViewById(R.id.org_login);
        CheckBox enabledBox = (CheckBox) rowView.findViewById(R.id.org_enabled);

        Drawable[] imgs = {
                context.getResources().getDrawable(R.drawable.vodakryma),
                context.getResources().getDrawable(R.drawable.krymgazseti),
                context.getResources().getDrawable(R.drawable.krymenergo)
        };

        titleText.setText(services.get(position).getOrganization());
        imageView.setImageDrawable(imgs[services.get(position).getImageId()]);
        subtitleText.setText(services.get(position).getLogin());
        enabledBox.setChecked(services.get(position).getEnabled() == 1 ? true : false);

        return rowView;
    }
}