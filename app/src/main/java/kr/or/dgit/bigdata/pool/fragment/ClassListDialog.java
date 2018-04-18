package kr.or.dgit.bigdata.pool.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import kr.or.dgit.bigdata.pool.R;

/**
 * Created by DGIT3-10 on 2018-04-17.
 */

public class ClassListDialog extends DialogFragment {
    NoticeListDialogLIstener mListener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mListener = (NoticeListDialogLIstener) context;
        }catch (ClassCastException e){
            throw  new ClassCastException(context.toString()+"must implement Noticelistdialoglistener");
        }

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle= getArguments();
        return new AlertDialog.Builder(getActivity()).setItems(R.array.classboard_selected, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String[] classes = getResources().getStringArray(R.array.classboard_selected);
                mListener.onDialogClick(ClassListDialog.this, classes[i]);
            }
        }).create();
    }

   public interface NoticeListDialogLIstener{
        public void onDialogClick(DialogFragment dialog, String res);
    }
}
