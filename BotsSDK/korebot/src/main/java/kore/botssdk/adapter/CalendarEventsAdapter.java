package kore.botssdk.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.internal.fuseable.HasUpstreamObservableSource;
import kore.botssdk.R;
import kore.botssdk.activity.GenericWebViewActivity;
import kore.botssdk.dialogs.WidgetDialogActivity;
import kore.botssdk.event.KoreEventCenter;
import kore.botssdk.events.CancelEvent;
import kore.botssdk.fragment.ComposeFooterFragment;
import kore.botssdk.listener.ComposeFooterInterface;
import kore.botssdk.listener.RecyclerViewDataAccessor;
import kore.botssdk.listener.VerticalListViewActionHelper;
import kore.botssdk.models.BotResponse;
import kore.botssdk.models.CalEventsTemplateModel;
import kore.botssdk.models.MeetingConfirmationModel;
import kore.botssdk.models.PayloadInner;
import kore.botssdk.models.WidgetDialogModel;
import kore.botssdk.utils.AppPermissionsHelper;
import kore.botssdk.utils.DateUtils;
import kore.botssdk.utils.KaFontUtils;
import kore.botssdk.utils.StringUtils;
import kore.botssdk.view.viewHolder.EmptyWidgetViewHolder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static kore.botssdk.utils.DateUtils.getDateinDayFormat;
import static kore.botssdk.utils.DateUtils.getTimeInAmPm;

/**
 * Created by Ramachandra Pradeep on 02-Aug-18.
 */

public class CalendarEventsAdapter extends RecyclerView.Adapter implements RecyclerViewDataAccessor {
    private boolean isExpanded = false;
    VerticalListViewActionHelper verticalListViewActionHelper;
    ArrayList<String> selectedIds = null;

    public ArrayList<CalEventsTemplateModel> getEventList() {
        return eventList;
    }

    public void setEventList(ArrayList<CalEventsTemplateModel> eventList) {
        if (eventList != null) {
            this.eventList.clear();
            this.eventList.addAll(eventList);
            this.title = "SHOW MORE";

        }
    }

    ArrayList<CalEventsTemplateModel> eventList = new ArrayList<>();
    private LayoutInflater inflater = null;
    private int EVENTS_LIST_LIMIT = 3;
    private String title = "SHOW MORE";
    private EventSelectionListener eventSelectionListener;
    private Context mContext;


    private int DATA_FOUND = 1;
    private int EMPTY_CARD = 0;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private String type;
    private boolean isEnabled;
    private ComposeFooterInterface composeFooterInterface;
    private Gson gson = new Gson();
    private boolean isFromWidget;

    public boolean isFromWidget() {
        return isFromWidget;
    }

    Drawable selectedCheck;
    Drawable unSelectedCheck;

    public void setFromWidget(boolean fromWidget) {
        isFromWidget = fromWidget;
    }


    public CalendarEventsAdapter(Context mContext, String type, boolean isEnabled) {
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
        this.type = type;
        this.isEnabled = isEnabled;
        notifyDataSetChanged();
        selectedIds = new ArrayList<>();
        selectedCheck = mContext.getResources().getDrawable(R.mipmap.checkbox_on);
        unSelectedCheck = mContext.getResources().getDrawable(R.mipmap.checkbox_off);
//        EVENTS_LIST_LIMIT = 3;
//        title = "SHOW MORE";txtDateAndTime
    }

    public void clearSelectedItems() {
        selectedIds.clear();
    }

    public CalEventsTemplateModel getItem(int position) {
        if (position < eventList.size())
            return eventList.get(position);
        else return null;
    }


    @Override
    public int getItemViewType(int position) {
        if (eventList != null && eventList.size() > 0) {
            return DATA_FOUND;
        }

        return EMPTY_CARD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == EMPTY_CARD) {


            View view = inflater.inflate(R.layout.card_empty_widget_layout, parent, false);
            return new EmptyWidgetViewHolder(view);
        } else
            return new ViewHolder(inflater.inflate(R.layout.calendar_event_list_item, parent, false));
    }


    public String checkStringNull(String value) {

        if (value != null && !value.trim().equalsIgnoreCase("")) {
            return value.trim();
        }
        return "";


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holderData, int position) {
        if (holderData.getItemViewType() == EMPTY_CARD) {
            EmptyWidgetViewHolder emptyHolder = (EmptyWidgetViewHolder) holderData;

            emptyHolder.tv_disrcription.setText("No Upcoming Meetings");
            emptyHolder.img_icon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.no_meeting));


        } else {

            ViewHolder holder = (ViewHolder) holderData;
            final CalEventsTemplateModel model = (CalEventsTemplateModel) eventList.get(position);
            //  holder.rowIndex.setText("" + (position + 1));
            String date = DateUtils.calendar_event_list_format1.format(model.getDuration().getStart()).toUpperCase();
            holder.txtDateTime.setText(date);

            if (selectedIds.size() > 0) {
                holder.checkbox.setVisibility(VISIBLE);
                holder.checkbox.setImageDrawable(selectedIds.contains(model.getEventId()) ? selectedCheck : unSelectedCheck);
            } else {
                holder.checkbox.setVisibility(GONE);
            }


            holder.txtTitle.setText(model.getTitle());
            // holder.txtPlace.setText(model.getWhere());
            if (!StringUtils.isNullOrEmptyWithTrim(model.getWhere())) {
                holder.txtPlace.setText(model.getWhere());
                holder.txtPlace.setVisibility(VISIBLE);

            } else {
                holder.txtPlace.setVisibility(GONE);

            }
            holder.tv_time.setText(DateUtils.calendar_list_format_2.format(model.getDuration().getStart()) + "\n" + DateUtils.calendar_list_format_2.format(model.getDuration().getEnd()));

            holder.tv_users.setText(getFormatedAttendiesFromList(model.getAttendees()));
            if (position == 0 || model.isShowDate()) {
                holder.tvborder.setVisibility(VISIBLE);
                holder.txtDateTime.setVisibility(VISIBLE);
            } else {
                holder.tvborder.setVisibility(GONE);
                holder.txtDateTime.setVisibility(GONE);
            }

            holder.sideBar.setBackgroundColor(Color.parseColor(model.getColor()));
            //  holder.layoutDetails.setBackgroundColor((Color.parseColor(model.getColor()) & 0x00ffffff) | (26 << 24));


            holder.innerlayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (verticalListViewActionHelper != null && isFromWidget()) {
                        if (selectedIds.contains(model.getEventId())) {
                            selectedIds.remove(model.getEventId());
                        } else {
                            selectedIds.add(model.getEventId());

                        }
                        verticalListViewActionHelper.widgetItemSelected(true, selectedIds.size());
                        notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });
            holder.innerlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (BotResponse.TEMPLATE_TYPE_CAL_EVENTS_WIDGET.equalsIgnoreCase(type) || isFromWidget()) {
                        //from left widget click

                        if (selectedIds != null && selectedIds.size() > 0 && verticalListViewActionHelper != null) {
                            // multiple item can be selected after long press and single click on other items
                            if (selectedIds.contains(model.getEventId())) {
                                selectedIds.remove(model.getEventId());
                            } else {
                                selectedIds.add(model.getEventId());
                            }
                            verticalListViewActionHelper.widgetItemSelected(true, selectedIds.size());
                            notifyDataSetChanged();

                        } else {
                            WidgetDialogModel widgetDialogModel = new WidgetDialogModel();
                            widgetDialogModel.setAttendies(checkStringNull(holder.tv_users.getText() != null ? holder.tv_users.getText().toString().trim() : ""));
                            widgetDialogModel.setLocation(checkStringNull(holder.txtPlace.getText() != null ? holder.txtPlace.getText().toString().trim() : ""));
                            widgetDialogModel.setTime(checkStringNull(holder.tv_time.getText() != null ? holder.tv_time.getText().toString().trim() : ""));
                            widgetDialogModel.setTitle(checkStringNull(holder.txtTitle.getText() != null ? holder.txtTitle.getText().toString().trim() : ""));
                            widgetDialogModel.setColor(checkStringNull(model.getColor()));

                            WidgetDialogActivity dialogActivity = new WidgetDialogActivity(mContext, widgetDialogModel, model);

                            dialogActivity.show();



                            dialogActivity.findViewById(R.id.img_cancel).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    dialogActivity.dissmissanim();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            dialogActivity.dismiss();
                                        }
                                    }, 400);

                                }
                            });
                        }
                    } else if (BotResponse.TEMPLATE_TYPE_CAL_EVENTS.equalsIgnoreCase(type)) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (AppPermissionsHelper.hasPermission(mContext, Manifest.permission.READ_CALENDAR)) {
                                    launchNativeView(model.getTitle(), (long) model.getDuration().getStart());
                                } else {
                                    gModel = model;
                                    AppPermissionsHelper.requestForPermission((Activity) mContext, Manifest.permission.READ_CALENDAR, CAL_PERMISSION_REQUEST);
                                }
                            } else {

                                launchNativeView(model.getTitle(), (long) model.getDuration().getStart());
                            }

                        } catch (Exception e) {

                            launchWebView(model.getHtmlLink());
                        }

                    } else if (isEnabled) {

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("meetingId", model.getEventId());
                        String message = "Cancel \"" + model.getTitle() + "\" " + getDateinDayFormat((long) model.getDuration().getStart()) + ", " + getTimeInAmPm((long) model.getDuration().getStart()) + " - " + getTimeInAmPm((long) model.getDuration().getEnd());
                        if (composeFooterInterface != null) {
                            composeFooterInterface.sendWithSomeDelay(message, gson.toJson(hashMap), 0);
                        } else {
                            KoreEventCenter.post(new CancelEvent(message, gson.toJson(hashMap), 0));
                            ((Activity) mContext).finish();
                        }
                    }
                }
            });
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return eventList != null && eventList.size() > 0 ? (!isExpanded && eventList.size() > 3 ? 3 : eventList.size()) : 1;
    }


    public ComposeFooterInterface getComposeFooterInterface() {
        return composeFooterInterface;
    }

    public void setComposeFooterInterface(ComposeFooterInterface composeFooterInterface) {
        this.composeFooterInterface = composeFooterInterface;
    }

    @Override
    public ArrayList getData() {
        return eventList;
    }

    @Override
    public void setData(ArrayList data) {
        this.eventList = data;
        if (eventList != null) {
            this.eventList = sortEventList(eventList);
        }
        notifyDataSetChanged();

    }

    public ArrayList<CalEventsTemplateModel> sortEventList(ArrayList<CalEventsTemplateModel> eventList) {

        ArrayList<CalEventsTemplateModel> newSortedData = new ArrayList<>();
        LinkedHashMap<String, ArrayList<CalEventsTemplateModel>> list = new LinkedHashMap<>();
        Collections.sort(eventList, new Comparator<CalEventsTemplateModel>() {
            public int compare(CalEventsTemplateModel o1, CalEventsTemplateModel o2) {

                DateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
                try {
                    return format.parse(o1.getDuration().getStart() + "").compareTo(format.parse(o2.getDuration().getStart() + ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }


            }
        });
        for (CalEventsTemplateModel data : eventList) {
            String date = DateUtils.calendar_event_list_format1.format(data.getDuration().getStart()).toUpperCase();
            ArrayList<CalEventsTemplateModel> sortmap = list.get(date);
            if (sortmap == null) {
                ArrayList<CalEventsTemplateModel> temp = new ArrayList<>();
                data.setShowDate(true);
                temp.add(data);
                list.put(date, temp);

            } else {
                sortmap.add(data);
                list.put(date, sortmap);
            }
        }
        Set<String> keys = list.keySet();
        for (String k : keys) {
            newSortedData.addAll(list.get(k));
        }
        return newSortedData;
    }

    @Override
    public void setExpanded(boolean isExpanded) {
        this.isExpanded = isExpanded;
    }

    @Override
    public void setVerticalListViewActionHelper(VerticalListViewActionHelper verticalListViewActionHelper) {
        this.verticalListViewActionHelper = verticalListViewActionHelper;

    }

    public interface EventSelectionListener {
        void onEventSelected(String url);
    }

    public void setMoreSelectionListener(EventSelectionListener eventSelectionListener) {
        this.eventSelectionListener = eventSelectionListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView rowIndex;
        TextView txtDateTime;
        LinearLayout layoutDetails, innerlayout;
        public View sideBar;
        public TextView txtTitle;
        public TextView txtPlace;
        public TextView tv_time;
        public TextView tvborder, tv_users;
        public ImageView checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDateTime = (TextView) itemView.findViewById(R.id.txtDateAndTime);
            layoutDetails = (LinearLayout) itemView.findViewById(R.id.layout_deails);
            innerlayout = (LinearLayout) itemView.findViewById(R.id.innerlayout);

            sideBar = itemView.findViewById(R.id.sideBar);

            txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            txtPlace = (TextView) itemView.findViewById(R.id.txtPlace);
            tvborder = (TextView) itemView.findViewById(R.id.tvborder);
            tv_users = (TextView) itemView.findViewById(R.id.tv_users);
            checkbox = itemView.findViewById(R.id.checkbox);

        }
    }


    private CalEventsTemplateModel gModel;
    private final int CAL_PERMISSION_REQUEST = 3221;


    private void launchNativeView(String title, long beginTime) throws Exception {
        int id = listSelectedCalendars(title, beginTime);
        if (id <= 0) throw new Exception("Invalid event id");

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("content://com.android.calendar/events/" + String.valueOf(id)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_NO_HISTORY
                    | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime);
            mContext.startActivity(intent);
        } catch (Exception e) {
            throw new Exception("Invalid event id");
        }
    }

    private void launchWebView(String htmlLink) {
        Intent intent = new Intent(mContext, GenericWebViewActivity.class);
        intent.putExtra("url", htmlLink);
        intent.putExtra("header", mContext.getResources().getString(R.string.app_name));
        mContext.startActivity(intent);
    }

    private static final int EVENT_INDEX_ID = 0;
    private static final int EVENT_INDEX_START = 1;
    private static final int EVENT_INDEX_END = 2;
    private static final int EVENT_INDEX_DURATION = 3;

    private static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Events._ID,      // 0
            CalendarContract.Events.DTSTART,  // 1
            CalendarContract.Events.DTEND,    // 2
            CalendarContract.Events.DURATION, // 3
    };

    private String getFormatedAttendiesFromList(List<CalEventsTemplateModel.Attendee> userDetailModels) {
        String users = "";
        if (userDetailModels != null && userDetailModels.size() > 0) {
            if (userDetailModels.size() == 1) {

                return userDetailModels.get(0).getName() != null ? userDetailModels.get(0).getName() : userDetailModels.get(0).getEmail();
            } else {
                int remaining = userDetailModels.size() - 1;
                return String.format("%1$s and %2$d others",
                        userDetailModels.get(0).getName() != null ? userDetailModels.get(0).getName() : userDetailModels.get(0).getEmail(), remaining);

            }
        }


        return users;

    }
    /*private void openCalendar(Uri uri) {
        String[] eidParts = extractEidAndEmail(uri);
        *//*if (uri != null) {
            String[] eidParts = extractEidAndEmail(uri);
            if (debug) Log.d(TAG, "eidParts=" + eidParts);
            String eid = uri.getQueryParameter("eid");
            if (eid != null) {
                ContentResolver cr = mContext.getContentResolver();
                final String selection = CalendarContract.Events._SYNC_ID + " LIKE \"%" + eidParts[0]
                        + "\" AND " + CalendarContract.Calendars.OWNER_ACCOUNT + " LIKE \"" + eidParts[1] + "\"";

                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Cursor eventCursor = cr.query(CalendarContract.Events.CONTENT_URI, EVENT_PROJECTION,
                        null, null, null);
                if(eventCursor != null){
                    Log.d("IKIDO",""+eventCursor.getCount());
                }
            }


        }*//*
        Log.d(TAG, "eidParts=" + eidParts[0]);
        CalendarService.readCalendar(mContext,218,0);
    }*/

    private int listSelectedCalendars(String eventtitle, long beginTime) {


        Uri eventUri;
        if (android.os.Build.VERSION.SDK_INT <= 7) {
            // the old way

            eventUri = Uri.parse("content://calendar/events");
        } else {
            // the new way

            eventUri = Uri.parse("content://com.android.calendar/events");
        }

        int result = -1;
        ContentResolver contentResolver = mContext.getContentResolver();

        /*// Create a cursor and read from the calendar (for Android API below 4.0)
        final Cursor cursor = contentResolver.query(Uri.parse("content://com.android.calendar/calendars"),
                (new String[] { "_id", "displayName", "selected" }), null, null, null);*/

     /*   Cursor lcursor = contentResolver.query(eventUri,
                new String[]{ "calendar_id", "title", "description", "dtstart", "dtend", "eventLocation" },
                null, null, null);

*/
        // Create a set containing all of the calendar IDs available on the phone
//        HashSet<String> calendarIds = CalendarService.getCalenderIds(lcursor);
//        beginTime = beginTime + TimeZone.getDefault().getRawOffset();
//        for(String id:calendarIds) {
        String projection[] = {"_id", "title", CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND};
        Cursor cursor = mContext.getContentResolver().query(eventUri, null, null,
                null, null);

        if (cursor.moveToFirst()) {

            String calName;
            String calID;
            String startTime;
            //    String endTime;

            int nameCol = cursor.getColumnIndex(projection[1]);
            int idCol = cursor.getColumnIndex(projection[0]);
            int startTimeCol = cursor.getColumnIndex(projection[2]);
            //     int endTimeCol = cursor.getColumnIndex(projection[3]);
            do {
                calName = cursor.getString(nameCol);
                calID = cursor.getString(idCol);
                startTime = cursor.getString(startTimeCol);
                //   endTime = cursor.getString(endTimeCol);


                if (calName != null && calName.equals(eventtitle)) {
                    if (Long.parseLong(startTime) == beginTime) {
//                        boolean val = (Long.parseLong(startTime) == sTime && Long.parseLong(endTime) == eTime);
                        return Integer.parseInt(calID);
                    }
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
//        }

        return result;

    }

}
