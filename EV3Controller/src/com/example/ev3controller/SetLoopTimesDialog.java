package com.example.ev3controller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

public class SetLoopTimesDialog extends DialogFragment {
	int forNum;
	NumberPicker np;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.loop_dialog, null, false);

		np = (NumberPicker) view.findViewById(R.id.numberPicker);
		np.setMaxValue(99);
		np.setMinValue(2);
		np.setValue(forNum-100);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// OKクリック時の処理
				ProgrammingActivity callingActivity = (ProgrammingActivity) getActivity();
				callingActivity.onReturnValue("FORNUM:"+np.getValue());
			}
		});
		builder.setNegativeButton("Cancel", null);
		builder.setView(view);
		return builder.create();
	}
	
	public void setForNum(Block sBlock){
		forNum = sBlock.getBlockType();
	}
}
