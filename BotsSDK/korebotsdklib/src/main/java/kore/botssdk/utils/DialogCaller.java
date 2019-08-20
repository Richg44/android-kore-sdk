package kore.botssdk.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;

public class DialogCaller {

public static void showDialog(Context context,
                              DialogInterface.OnClickListener onClickListener) {

    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
    dialog.setCancelable(false);
    dialog.setMessage(Html.fromHtml("This action will end your conversation with <b>" + Constants.SKILL_SELECTION +
            "</b> and move to "+ Constants.SKILL_HOME+". Do you want to continue?"));



   /* dialog.setMessage(Html.fromHtml("This action will end your conversation with <b>" + Constants.SKILL_SELECTION +
            "</b> skill and move to Kora. Do you want to continue?"));*/
    dialog.setPositiveButton("Yes",onClickListener);
    dialog.setNegativeButton("No",null);
    dialog.show();
}
}