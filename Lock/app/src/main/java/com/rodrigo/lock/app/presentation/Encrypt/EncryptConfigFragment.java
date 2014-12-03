package com.rodrigo.lock.app.presentation.Encrypt;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.rodrigo.lock.app.Core.Clases.FileHeader;
import com.rodrigo.lock.app.R;

//import org.jraf.android.backport.switchwidget.Switch;

import java.text.ParseException;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * Created by Rodrigo on 12/07/2014.
 */
public class EncryptConfigFragment extends Fragment implements  OnDateSetListener {

    @InjectView(R.id.fecha)   TextView fecha;
    @InjectView(R.id.cifrar)   Switch cifrar;
    @InjectView(R.id.vencimiento)   Switch vencimiento;
    @InjectView(R.id.layout_vencimiento)   LinearLayout layout_vencimiento;
    @InjectView(R.id.soloaca)   Switch soloaca;
    @InjectView(R.id.prhoibirextraer)   Switch prhoibirextraer;
    @InjectView(R.id.dejarcopiasinbloquear)   Switch dejarcopiasinbloquear;


    DatePickerDialog datePickerDialog = null;
    ReceiveAndEncryptActivity padre;
    public static final String DATEPICKER_TAG = "datepicker";

    public EncryptConfigFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View V = inflater.inflate(R.layout.fragment_config_encrypt, container, false);
        ButterKnife.inject(this, V);
        padre = (ReceiveAndEncryptActivity) this.getActivity();

        final Calendar calendar = Calendar.getInstance();
        datePickerDialog =DatePickerDialog.newInstance(this, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), false);


        if (savedInstanceState != null) {
            DatePickerDialog dpd = (DatePickerDialog) padre.getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
            if (dpd != null) {
                dpd.setOnDateSetListener(this );
            }
        }

        return V;
    }



    @Override
    public void onResume() {
        super.onResume();
        vistaComoCavezal();
    }



    public void vistaComoCavezal(){
        FileHeader c = padre.getCabezal();
        cifrar.setChecked(c.isCifrar());
        vencimiento.setChecked(c.isCaducidad());
        soloaca.setChecked(c.isSoloAca());
        prhoibirextraer.setChecked(c.isProhibirExtraer());
        dejarcopiasinbloquear.setChecked(c.isCopiaSinBloquear());
    }



    @OnCheckedChanged(R.id.cifrar)
    public void setCifrar() {
        if (cifrar.isChecked()){
            this.padre.getCabezal().setCifrar(true);
            soloaca.setEnabled(true);
        }else{
            this.padre.getCabezal().setCifrar(false);
            soloaca.setEnabled(false);
            soloaca.setChecked(false);
        }
    }

    @OnCheckedChanged(R.id.soloaca)
    public void abrirsoloaca() {
        this.padre.getCabezal().setSoloAca(soloaca.isChecked());
    }


    @OnCheckedChanged(R.id.prhoibirextraer)
    public void setPrhoibirextraer() {
        this.padre.getCabezal().setProhibirExtraer(prhoibirextraer.isChecked());
    }

    @OnCheckedChanged(R.id.dejarcopiasinbloquear)
    public void setDejarcopiasinbloquear() {
        this.padre.getCabezal().setCopiaSinBloquear(dejarcopiasinbloquear.isChecked());
    }


    @OnCheckedChanged(R.id.vencimiento)
    public void regularCaducidad() {
        this.padre.getCabezal().setCaducidad(vencimiento.isChecked());
        if (vencimiento.isChecked()) {
            layout_vencimiento.setVisibility(View.VISIBLE);
            this.padre.getCabezal().setCaducidad(true);
            try {
                fecha.setText(this.padre.getCabezal().getFechaCaducidadFormat());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            layout_vencimiento.setVisibility(View.GONE);
            this.padre.getCabezal().setCaducidad(false);
        }
    }




    @OnClick(R.id.vencimiento)
    public void vencimiento() {
        regularCaducidad();
        if (vencimiento.isChecked()) {
            datePickerDialog.setVibrate(false);
            datePickerDialog.setYearRange(2002, 2028);
            //datePickerDialog.setCloseOnSingleTapDay(true);
            datePickerDialog.show(padre.getSupportFragmentManager(), DATEPICKER_TAG);

        }
    }

    @OnClick(R.id.fecha)
    public void cambiarFecha() {
       vencimiento();
    }





    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {
        //datePickerDialog.
        month++;
        FileHeader c = this.padre.getCabezal();
        c.setCaducidad(true);
        c.setFechaCaducidad(year, month,day);
        regularCaducidad();
        String vencimiento = day + "/" + month + "/"  + year;
        Toast.makeText(this.padre, String.format(getResources().getString(R.string.new_expiration), vencimiento)  , Toast.LENGTH_LONG).show();
    }








}