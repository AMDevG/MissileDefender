package com.johnberry.missiledefender;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import org.json.JSONException;

import java.sql.SQLException;


public class DialogAPI extends AppCompatDialogFragment {

    private TextView initialsText;
    private DialogListener listener;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.sign_in_dialog,null);
        builder.setView(view)
                .setTitle("Top Score!")

                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                         String initialsIn = initialsText.getText().toString();

                        try {
                            listener.applyTexts(initialsIn);
                        } catch (SQLException throwables) {
                            throwables.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });

        initialsText = view.findViewById(R.id.initialsText);

        return builder.create();

    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        listener = (DialogListener) context;
    }

    public interface DialogListener{
        void applyTexts(String userInitials) throws SQLException, JSONException, ClassNotFoundException;
    }
}
