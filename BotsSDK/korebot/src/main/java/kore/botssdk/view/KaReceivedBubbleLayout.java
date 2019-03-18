package kore.botssdk.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import kore.botssdk.R;
import kore.botssdk.models.AttendeeSlotTemplateModel;
import kore.botssdk.models.BaseBotMessage;
import kore.botssdk.models.BotPieChartElementModel;
import kore.botssdk.models.BotResponse;
import kore.botssdk.models.CalEventsTemplateModel;
import kore.botssdk.models.ComponentModel;
import kore.botssdk.models.ContactInfoModel;
import kore.botssdk.models.KaFileLookupModel;
import kore.botssdk.models.KnowledgeDetailModel;
import kore.botssdk.models.MeetingConfirmationModel;
import kore.botssdk.models.MeetingTemplateModel;
import kore.botssdk.models.PayloadInner;
import kore.botssdk.models.PayloadOuter;
import kore.botssdk.models.TaskTemplateResponse;
import kore.botssdk.net.SDKConfiguration;
import kore.botssdk.utils.DateUtils;
import kore.botssdk.utils.StringUtils;
import kore.botssdk.view.viewUtils.BubbleViewUtil;
import kore.botssdk.view.viewUtils.LayoutUtils;
import kore.botssdk.view.viewUtils.MeasureUtils;

import static kore.botssdk.net.SDKConfiguration.BubbleColors.BubbleUI;


/**
 * Created by Pradeep Mahato on 01-Jun-16.
 * Copyright (c) 2014 Kore Inc. All rights reserved.
 */
public class KaReceivedBubbleLayout extends KaBaseBubbleLayout {

    CircularProfileView cpvSenderImage;
    int carouselViewHeight, pieViewHeight, tableHeight, lineHeight;
    ArrayList<Integer> arrayList = new ArrayList<>();

    public KaReceivedBubbleLayout(Context context) {
        super(context);
        init();
    }

    public KaReceivedBubbleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KaReceivedBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public KaReceivedBubbleLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public int getLinkTextColor() {
        return LEFT_BUBBLE_LINK_COLOR;
    }

    private void init() {
        textMediaLayoutGravity = TextMediaLayout.GRAVITY_LEFT;
        carouselViewHeight = (int) getResources().getDimension(R.dimen.carousel_layout_height);
        pieViewHeight = (int) getResources().getDimension(R.dimen.pie_layout_height);
        tableHeight = (int) getResources().getDimension(R.dimen.table_layout_height);
        lineHeight = (int) getResources().getDimension(R.dimen.line_layout_height);
        super.setLeftSide(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        cpvSenderImage = (CircularProfileView) findViewById(R.id.cpvSenderImage);
    }

    @Override
    void initializeBubbleBorderPass1() {

        BUBBLE_LEFT_PROFILE_PIC = cpvSenderImage.getMeasuredWidth();
        BUBBLE_LEFT_PROFILE_PIC_MARGIN_LEFT = (BUBBLE_LEFT_PROFILE_PIC != 0) ? (int) (10 * dp1) : 0;
        BUBBLE_LEFT_PROFILE_PIC_MARGIN_RIGHT = (BUBBLE_LEFT_PROFILE_PIC != 0) ? 0 : 0;
        if (isContinuousMessage) {
            BUBBLE_TOP_BORDER = 0;
        } else {
            BUBBLE_TOP_BORDER = (int) (10 * dp1);
        }
        BUBBLE_LEFT_BORDER = (int) ((!isGroupMessage) ? BubbleUI ? dp4 : 0 : dp1);
        BUBBLE_RIGHT_BORDER = (int) dp1;
        BUBBLE_DOWN_BORDER = (int) dp1;
        BUBBLE_ARROW_WIDTH = (int) ((isGroupMessage) ? dp1 : BubbleUI ? dp10 : 0);
        BUBBLE_LEFT_ARROW_WIDTH = (int) (BubbleUI ? BUBBLE_ARROW_WIDTH / 2 + 7 * dp1 : 0);
        BUBBLE_RIGHT_ARROW_WIDTH = 0;
        if (bubbleTextMediaLayout.getMeasuredHeight() != 0) {
            BUBBLE_CONTENT_TOP_MARGIN = 0;
            BUBBLE_CONTENT_BOTTOM_MARGIN = (int) (BubbleUI ? 8 * dp1 : 21 * dp1);
        } else {
            BUBBLE_CONTENT_TOP_MARGIN = 0;
            BUBBLE_CONTENT_BOTTOM_MARGIN = 0;
        }
        BUBBLE_CAROUSEL_BOTTOM_SHADE_MARGIN = (int) getResources().getDimension(R.dimen.carousel_view_cardCornerRadius);
    }


    @Override
    void initializeBubbleBorderPass2() {
        BUBBLE_CONTENT_RIGHT_BORDER = 0; //this is always 0...
        BUBBLE_CONTENT_LEFT_BORDER = 0; //this is always 0...

        //  invalidate();
    }

    @Override
    protected void initializeBubbleContentDimen() {
        super.initializeBubbleContentDimen();

        // headerLayoutDimen[0] = BUBBLE_LEFT_BORDER + BUBBLE_LEFT_PROFILE_PIC_MARGIN_LEFT + BUBBLE_LEFT_PROFILE_PIC + BUBBLE_LEFT_PROFILE_PIC_MARGIN_RIGHT + BUBBLE_LEFT_ARROW_WIDTH + headerLayout.getMeasuredWidth();
        maxContentDimen[0] = BUBBLE_LEFT_BORDER + BUBBLE_LEFT_PROFILE_PIC_MARGIN_LEFT + BUBBLE_LEFT_PROFILE_PIC + BUBBLE_LEFT_PROFILE_PIC_MARGIN_RIGHT
                + BUBBLE_LEFT_ARROW_WIDTH + BUBBLE_CONTENT_LEFT_MARGIN + Collections.max(Arrays.asList(textMediaDimen[0], botCarouselView.getMeasuredWidth(), verticalListView.getMeasuredWidth(), timeStampsTextView.getMeasuredWidth(), timeLineView.getMeasuredWidth(),
                meetingSlotsView.getMeasuredWidth(), attendeeSlotSelectionView.getMeasuredWidth(), meetingConfirmationView.getMeasuredWidth(), botButtonView.getMeasuredWidth(), tableView.getMeasuredWidth(), lineChartView.getMeasuredWidth(),
                botListTemplateView.getMeasuredWidth(), contactInfoView.getMeasuredWidth(), botPieChartView.getMeasuredWidth())) + BUBBLE_CONTENT_RIGHT_MARGIN + BUBBLE_RIGHT_ARROW_WIDTH + BUBBLE_RIGHT_BORDER;


        // headerLayoutDimen[1] = headerLayout.getMeasuredHeight();
        maxBubbleDimen[0] = maxContentDimen[0];

        maxBubbleDimen[1] = BUBBLE_SEPARATION_DISTANCE + BUBBLE_TOP_BORDER + BUBBLE_CONTENT_TOP_MARGIN +
                textMediaDimen[1] + botCarouselView.getMeasuredHeight() + meetingSlotsView.getMeasuredHeight() + verticalListView.getMeasuredHeight() + cpvSenderImage.getMeasuredHeight() + timeLineView.getMeasuredHeight() +
                meetingConfirmationView.getMeasuredHeight() + attendeeSlotSelectionView.getMeasuredHeight() + botPieChartView.getMeasuredHeight() + tableView.getMeasuredHeight() + lineChartView.getMeasuredHeight()
                + botButtonView.getMeasuredHeight() + botListTemplateView.getMeasuredHeight() + contactInfoView.getMeasuredHeight()
                + BUBBLE_CONTENT_BOTTOM_MARGIN + BUBBLE_DOWN_BORDER + (int) (botButtonView.getMeasuredHeight() != 0 ||
                meetingSlotsView.getMeasuredHeight() != 0 ? dp2 : 0);
        if (bubbleTextMediaLayout.getMeasuredHeight() != 0 && isTemplatePresent()) {
            maxBubbleDimen[1] = maxBubbleDimen[1] + (int) dp15;
        }
        maxContentDimen[1] = BUBBLE_CONTENT_TOP_MARGIN + textMediaDimen[1] + botCarouselView.getMeasuredHeight() + verticalListView.getMeasuredHeight()
                + cpvSenderImage.getMeasuredHeight() + timeLineView.getMeasuredHeight() + botButtonView.getMeasuredHeight() + botListTemplateView.getMeasuredHeight() + botPieChartView.getMeasuredHeight() +
                tableView.getMeasuredHeight() + lineChartView.getMeasuredHeight() + meetingSlotsView.getMeasuredHeight() + attendeeSlotSelectionView.getMeasuredHeight() +
                +meetingConfirmationView.getMeasuredHeight() + contactInfoView.getMeasuredHeight() + BUBBLE_CONTENT_BOTTOM_MARGIN;
        if (bubbleTextMediaLayout.getMeasuredHeight() != 0 && isTemplatePresent()) {
            maxBubbleDimen[1] = maxBubbleDimen[1] + (int) dp15;
        }
    }

/*    @Override
    protected void populateHeaderLayout(int position, BaseBotMessage baseBotMessage) {
        try {
            headerLayout.populateHeader(KoreLibDateUtils.formattedSentDateV6(DateUtils.getTimeStamp(baseBotMessage.getCreatedOn(), true)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private boolean isTemplatePresent() {
        return contactInfoView.getMeasuredHeight() > 0 || botListTemplateView.getMeasuredHeight() > 0 || botButtonView.getMeasuredHeight() > 0 || botCarouselView.getMeasuredHeight() > 0 || attendeeSlotSelectionView.getMeasuredHeight() > 0 || meetingConfirmationView.getMeasuredHeight() > 0 || verticalListView.getMeasuredHeight() > 0 || meetingSlotsView.getMeasuredHeight() > 0 || botPieChartView.getMeasuredHeight() > 0 || lineChartView.getMeasuredHeight() > 0 || tableView.getMeasuredHeight() > 0;
    }

    @Override
    protected void preCosmeticChanges() {
        super.preCosmeticChanges();
        resetAll();

    }

    private void resetAll() {
        arrayList.clear();
        botButtonView.setVisibility(View.GONE);
        botButtonView.populateButtonList(null,false);
        botCarouselView.populateCarouselView(null);
        botCarouselView.setVisibility(View.GONE);
        verticalListView.prepareDataSetAndPopulate(null, null, false);
        verticalListView.setVisibility(GONE);
        meetingSlotsView.populateData(null, false);
        meetingSlotsView.setVisibility(GONE);
        attendeeSlotSelectionView.populateData(-1, null, false);
        attendeeSlotSelectionView.setVisibility(GONE);
        meetingConfirmationView.populateData(null);
        meetingConfirmationView.setVisibility(GONE);
        contactInfoView.populateData(null);
        contactInfoView.setVisibility(GONE);
        botListTemplateView.setVisibility(View.GONE);
        botListTemplateView.populateListTemplateView(null, null);
        botPieChartView.setVisibility(View.GONE);
        tableView.setVisibility(View.GONE);
        lineChartView.setVisibility(GONE);
        timeLineView.setVisibility(GONE);
        timeLineView.setText("");
    }

    @Override
    protected void cosmeticChanges(BaseBotMessage baseBotMessage) {
        super.cosmeticChanges(baseBotMessage);
        cosmetiseForProfilePic(baseBotMessage);
    }


    protected void cosmetiseForProfilePic(BaseBotMessage baseBotMessage) {
/*        if (isGroupMessage) {
            String icon = ((BotResponse) baseBotMessage).getIcon();
            cpvSenderImage.setVisibility(VISIBLE);
            cpvSenderImage.populateLayout(" ", null, icon, null, SDKConfiguration.BubbleColors.getIcon(), 0, true, BUBBLE_LEFT_PROFILE_PIC, BUBBLE_LEFT_PROFILE_PIC);
        } else {
            cpvSenderImage.setVisibility(GONE);
        }*/
        String icon = ((BotResponse) baseBotMessage).getIcon();
        if(SDKConfiguration.BubbleColors.showIcon) {
            cpvSenderImage.populateLayout(" ", null, null, null, SDKConfiguration.BubbleColors.getIcon(), R.color.white, true, BUBBLE_LEFT_PROFILE_PIC, BUBBLE_LEFT_PROFILE_PIC);
            cpvSenderImage.setVisibility(StringUtils.isNullOrEmptyWithTrim(timeStampsTextView.getText()) ? GONE : VISIBLE);
        }else{
            cpvSenderImage.setVisibility(GONE);
        }
    }

    protected void populateForTemplates(int position, boolean isLastItem, ComponentModel compModel, BaseBotMessage baseBotMessage) {
        resetAll();
        if (compModel != null) {

            PayloadOuter payOuter = compModel.getPayload();
            if(payOuter == null) return;
            PayloadInner payInner;
            if (payOuter.getText() != null && payOuter.getText().contains("&quot")) {
//                Gson gson = new Gson();
                payOuter.getText().replace("&quot;", "\"");
            }
            payInner = payOuter.getPayload();
            if (payInner != null)
                payInner.convertElementToAppropriate();
            if (BotResponse.COMPONENT_TYPE_TEMPLATE.equalsIgnoreCase(payOuter.getType()) && payInner != null) {
                checkBubbleVisibilityAndHideCpv(payInner);
                if (BotResponse.TEMPLATE_TYPE_BUTTON.equalsIgnoreCase(payInner.getTemplate_type())) {
                    botButtonView.setVisibility(View.VISIBLE);
                    botButtonView.setRestrictedMaxWidth(BUBBLE_CONTENT_LEFT_MARGIN + BubbleViewUtil.getBubbleContentWidth() + BUBBLE_CONTENT_RIGHT_MARGIN);
                    botButtonView.populateButtonList(payInner.getButtons(),isLastItem);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_QUICK_REPLIES.equalsIgnoreCase(payInner.getTemplate_type()) || BotResponse.TEMPLATE_TYPE_FORM_ACTIONS.equalsIgnoreCase(payInner.getTemplate_type())) {
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    if (StringUtils.isNullOrEmptyWithTrim(payInner.getText())) {
                        timeStampsTextView.setText("");
                    }
                } else if (BotResponse.TEMPLATE_TYPE_CAROUSEL.equalsIgnoreCase(payInner.getTemplate_type()) || BotResponse.TEMPLATE_TYPE_WELCOME_CAROUSEL.equalsIgnoreCase(payInner.getTemplate_type())) {
                    botCarouselView.setVisibility(View.VISIBLE);
                    botCarouselView.populateCarouselView(payInner.getCarouselElements(), payInner.getTemplate_type());
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_LIST.equalsIgnoreCase(payInner.getTemplate_type())) {
                    botListTemplateView.setVisibility(View.VISIBLE);
                    botListTemplateView.setRestrictedMaxWidth(BUBBLE_CONTENT_LEFT_MARGIN + BubbleViewUtil.getBubbleContentWidth() + BUBBLE_CONTENT_RIGHT_MARGIN);
                    botListTemplateView.populateListTemplateView(payInner.getListElements(), payInner.getButtons());
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_PIECHART.equalsIgnoreCase(payInner.getTemplate_type())) {
                    botPieChartView.setVisibility(View.VISIBLE);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    ArrayList<BotPieChartElementModel> elementModels = payInner.getPieChartElements();
                    if (elementModels != null && !elementModels.isEmpty()) {
                        ArrayList<String> xVal = new ArrayList<>(elementModels.size());
                        ArrayList<PieEntry> yVal = new ArrayList<>(elementModels.size());
                        for (int i = 0; i < elementModels.size(); i++) {
                            xVal.add(elementModels.get(i).getTitle());
                            yVal.add(new PieEntry((float) elementModels.get(i).getValue(), i));
                        }
                        botPieChartView.populatePieChart("", payInner.getPie_type(), xVal, yVal);
                    }
                } else if (BotResponse.TEMPLATE_TYPE_TABLE.equalsIgnoreCase(payInner.getTemplate_type())) {
                    tableView.setVisibility(View.VISIBLE);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    tableView.setData(payInner.getTemplate_type(), payInner);

                } else if (BotResponse.TEMPLATE_TYPE_LINECHART.equalsIgnoreCase(payInner.getTemplate_type())) {
                    lineChartView.setVisibility(View.VISIBLE);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    lineChartView.setData(payInner);
                } else if (BotResponse.TEMPLATE_TYPE_KORA_CAROUSAL.equals(payInner.getTemplate_type())) {
                    ArrayList<KnowledgeDetailModel> knowledgeData = payInner.getKnowledgeDetailModels();
                    verticalListView.setVisibility(View.VISIBLE);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    verticalListView.prepareDataSetAndPopulate(knowledgeData, payInner.getTemplate_type(), isLastItem);
                } else if (BotResponse.TEMPLATE_TYPE_KORA_SEARCH_CAROUSAL.equalsIgnoreCase(payInner.getTemplate_type())) {
                    verticalListView.setVisibility(View.VISIBLE);
                    if (payInner.getKoraSearchResultsModel() != null)
                        verticalListView.prepareDataSetAndPopulate(payInner.getKoraSearchResultsModel().get(0).getEmails(), BotResponse.TEMPLATE_TYPE_KORA_SEARCH_CAROUSAL, isLastItem);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_AUTO_FORMS.equalsIgnoreCase(payInner.getTemplate_type())) {
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    if (StringUtils.isNullOrEmptyWithTrim(payInner.getText())) {
                        timeStampsTextView.setText("");
                    }
                } else if (BotResponse.TEMPLATE_TYPE_SLOT_PICKER.equalsIgnoreCase(payInner.getTemplate_type())) {
                    ArrayList<MeetingTemplateModel> meetingTemplateModels = payInner.getMeetingTemplateModels();
                    if (meetingTemplateModels != null && meetingTemplateModels.size() > 0) {
                        meetingSlotsView.setVisibility(View.VISIBLE);
                        meetingSlotsView.populateData(meetingTemplateModels.get(0), isLastItem);
                    }
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_MEETING_CONFIRM.equalsIgnoreCase(payInner.getTemplate_type())) {
                    ArrayList<MeetingConfirmationModel> meetingTemplateModels = payInner.getMeetingConfirmationModels();
                    if (meetingTemplateModels != null && meetingTemplateModels.size() > 0) {
                        meetingConfirmationView.setVisibility(View.VISIBLE);
                        meetingConfirmationView.populateData(meetingTemplateModels.get(0));
                        // BUBBLE_CONTENT_RIGHT_MARGIN = 0;
                    }
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_TASK_VIEW.equalsIgnoreCase(payInner.getTemplate_type()) || BotResponse.TEMPLATE_TASK_FULLVIEW.equalsIgnoreCase(payInner.getTemplate_type())) {
                    ArrayList<TaskTemplateResponse> taskTemplateModels = payInner.getTaskTemplateModels();
                    if (taskTemplateModels != null && taskTemplateModels.size() > 0) {
                        TaskTemplateResponse taskTemplateModel = taskTemplateModels.get(0);
                        verticalListView.setVisibility(VISIBLE);
                        verticalListView.prepareDataToTasks(taskTemplateModel, BotResponse.TEMPLATE_TYPE_TASK_VIEW, isLastItem && (taskTemplateModel.getButtons() != null && taskTemplateModel.getButtons().size() > 0));
                    }
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_ATTENDEE_SLOTS.equalsIgnoreCase(payInner.getTemplate_type())) {
                    ArrayList<AttendeeSlotTemplateModel> meetingTemplateModels = payInner.getAttendeeSlotTemplateModels();
                    if (meetingTemplateModels != null && meetingTemplateModels.size() > 0) {
                        attendeeSlotSelectionView.setVisibility(View.VISIBLE);
                        attendeeSlotSelectionView.populateData(position, meetingTemplateModels.get(0), isLastItem);
                    }
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_CAL_EVENTS.equalsIgnoreCase(payInner.getTemplate_type()) || BotResponse.TEMPLATE_TYPE_CANCEL_EVENT.equalsIgnoreCase(payInner.getTemplate_type())) {
                    ArrayList<CalEventsTemplateModel> calList = payInner.getCalEventsTemplateModels();
                    if (calList != null && !calList.isEmpty()) {
                        verticalListView.setVisibility(View.VISIBLE);
                        verticalListView.prepareDataSetAndPopulate(calList, payInner.getTemplate_type(), isLastItem);
                    }
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_FILES_LOOKUP.equalsIgnoreCase(payInner.getTemplate_type())) {
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    verticalListView.setVisibility(View.VISIBLE);
                    ArrayList<KaFileLookupModel> fileList = payInner.getFileLookupModels();
                    if (fileList != null)
                        verticalListView.prepareDataSetAndPopulate(fileList, BotResponse.TEMPLATE_TYPE_FILES_LOOKUP, isLastItem);
                } else if (BotResponse.KA_CONTACT_VIEW.equalsIgnoreCase(payInner.getTemplate_type())) {
                    contactInfoView.setVisibility(View.VISIBLE);
                    bubbleTextMediaLayout.populateText(payInner.getText());
                    ArrayList<ContactInfoModel> contactInfoModels = payInner.getContactInfoModels();
                    if (contactInfoModels != null)
                        contactInfoView.populateData(contactInfoModels.get(0));
                } else if (BotResponse.TEMPLATE_TYPE_CONVERSATION_END.equalsIgnoreCase(payInner.getTemplate_type())) {
                    timeStampsTextView.setText("");
                    timeLineView.setVisibility(VISIBLE);
                    timeLineView.setText(String.format("%s %s", getContext().getString(R.string.conversation_end), DateUtils.getTimeInAmPm(baseBotMessage.getCreatedInMillis())));
                } else if (BotResponse.KA_SWITCH_SKILL.equalsIgnoreCase(payInner.getTemplate_type())) {
                    timeStampsTextView.setText("");
                    timeLineView.setVisibility(VISIBLE);
                    timeLineView.setText(payInner.getText());
                } else if (BotResponse.TEMPLATE_TYPE_SHOW_PROGRESS.equalsIgnoreCase(payInner.getTemplate_type())) {
                    timeStampsTextView.setText("");
                } else if (BotResponse.TEMPLATE_TYPE_SESSION_END.equalsIgnoreCase(payInner.getTemplate_type())) {
                    timeStampsTextView.setText("");
                } else if (!StringUtils.isNullOrEmptyWithTrim(payInner.getText())) {
                    bubbleTextMediaLayout.populateText(payInner.getText());
                } else if (StringUtils.isNullOrEmptyWithTrim(payOuter.getText())) {
                    timeStampsTextView.setText("");
                }

            } else if (BotResponse.COMPONENT_TYPE_MESSAGE.equalsIgnoreCase(payOuter.getType()) && payInner != null) {
                bubbleTextMediaLayout.populateText(payInner.getText());
            } else {
                bubbleTextMediaLayout.populateText(payOuter.getText());

            }
        }
    }

    void checkBubbleVisibilityAndHideCpv(PayloadInner payloadInner) {
        //setDoDrawBubbleBackground(!(payloadInner.getText() == null || payloadInner.getText().isEmpty()));
        if (!isDoDrawBubbleBackground()) {
            cpvSenderImage.setVisibility(GONE);
        }
    }


    /**
     * onla
     * Layout Manipulation Section
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int maxAllowedWidth = parentWidth;
        int wrapSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);


        int childWidthSpec;
        int childHeightSpec;

        /*
         * For TextMedia Layout
         */
        childWidthSpec = MeasureSpec.makeMeasureSpec(maxAllowedWidth, MeasureSpec.AT_MOST);
        int fullWidthSpec = MeasureSpec.makeMeasureSpec(maxAllowedWidth, MeasureSpec.EXACTLY);
        MeasureUtils.measure(bubbleTextMediaLayout, childWidthSpec, wrapSpec);
        MeasureUtils.measure(meetingSlotsView, childWidthSpec, wrapSpec);
        MeasureUtils.measure(timeStampsTextView, wrapSpec, wrapSpec);
        MeasureUtils.measure(timeLineView, fullWidthSpec, wrapSpec);

        /*
         * For Sender icon [CPV]
         */
        float cpvSenderImageDimen = dp1 * 21;
        childWidthSpec = MeasureSpec.makeMeasureSpec((int) cpvSenderImageDimen, MeasureSpec.EXACTLY);
        childHeightSpec = MeasureSpec.makeMeasureSpec((int) cpvSenderImageDimen, MeasureSpec.EXACTLY);
        cpvSenderImage.setDimens(cpvSenderImageDimen, cpvSenderImageDimen);
        MeasureUtils.measure(cpvSenderImage, childWidthSpec, childHeightSpec);





        /*
         * For Button Templates
         */
        MeasureUtils.measure(botButtonView, wrapSpec, wrapSpec);

        /*
         * For List Templates
         */
        MeasureUtils.measure(botListTemplateView, wrapSpec, wrapSpec);

        /*For calendar events*/

        /**
         * For PieChart
         */
        childWidthSpec = MeasureSpec.makeMeasureSpec((int) screenWidth - 50 * (int) dp1, MeasureSpec.EXACTLY);
        childHeightSpec = MeasureSpec.makeMeasureSpec((int) (pieViewHeight), MeasureSpec.EXACTLY);
        MeasureUtils.measure(botPieChartView, childWidthSpec, childHeightSpec);


        /**
         * For TableViev
         */
        childWidthSpec = MeasureSpec.makeMeasureSpec((int) screenWidth - 50 * (int) dp1, MeasureSpec.EXACTLY);
        MeasureUtils.measure(tableView, childWidthSpec, wrapSpec);

        /**
         * for line chart
         */

        childWidthSpec = MeasureSpec.makeMeasureSpec((int) screenWidth - 50 * (int) dp1, MeasureSpec.EXACTLY);
        childHeightSpec = MeasureSpec.makeMeasureSpec((int) lineHeight, MeasureSpec.EXACTLY);
        MeasureUtils.measure(lineChartView, childWidthSpec, childHeightSpec);

        /*
         * For CarouselView
         */
        childWidthSpec = MeasureSpec.makeMeasureSpec((int) screenWidth, MeasureSpec.EXACTLY);
        //  childHeightSpec = MeasureSpec.makeMeasureSpec((int) (carouselViewHeight) + BUBBLE_CONTENT_BOTTOM_MARGIN , MeasureSpec.EXACTLY);
        MeasureUtils.measure(botCarouselView, childWidthSpec, wrapSpec);
        MeasureUtils.measure(meetingConfirmationView, childWidthSpec, wrapSpec);
        MeasureUtils.measure(verticalListView, childWidthSpec, wrapSpec);
        MeasureUtils.measure(attendeeSlotSelectionView, childWidthSpec, wrapSpec);
        MeasureUtils.measure(contactInfoView, childWidthSpec, wrapSpec);
        initializeBubbleDimensionalParametersPhase1(); //Initiliaze params

        int parentHeightSpec = MeasureSpec.makeMeasureSpec(maxBubbleDimen[1], MeasureSpec.EXACTLY);
        int parentWidthSpec = MeasureSpec.makeMeasureSpec(maxBubbleDimen[0], MeasureSpec.EXACTLY);

        super.onMeasure(parentWidthSpec, parentHeightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        //Consider the paddings and manupulate them it first..
        l += getPaddingLeft();
        t += getPaddingTop();
        r -= getPaddingRight();
        b -= getPaddingBottom();

        int top = 0, left = 0;
        int minimumTop = getPaddingTop() + BUBBLE_TOP_BORDER + BUBBLE_SEPARATION_DISTANCE;
        left = BUBBLE_LEFT_BORDER;
        top = minimumTop;


        int bubbleTextMediaLayouMarginLeft = BUBBLE_CONTENT_LEFT_MARGIN;
        int bubbleTextMediaLayouMarginTop = BUBBLE_CONTENT_TOP_MARGIN + BUBBLE_FORWARD_LAYOUT_HEIGHT_CONSIDERATION_FOR_PAINT;

        /*
         * For TextMedia Layout
         */
        left += bubbleTextMediaLayouMarginLeft;
        top = top + bubbleTextMediaLayouMarginTop;
        LayoutUtils.layoutChild(bubbleTextMediaLayout, left, top);
        arrayList.add(bubbleTextMediaLayout.getBottom());

        top = bubbleTextMediaLayout.getMeasuredHeight() != 0 ? bubbleTextMediaLayout.getBottom() + (int) dp15 : minimumTop;
        left = bubbleTextMediaLayout.getLeft() - (BubbleUI ? BUBBLE_CONTENT_LEFT_MARGIN : 0);

        layoutView(botButtonView, top, left, arrayList);
        layoutView(meetingSlotsView, top, left, arrayList);
        layoutView(attendeeSlotSelectionView, top, (int) (left - dp1), arrayList);
        layoutView(meetingConfirmationView, top, (int) (left - dp1), arrayList);
        layoutView(botCarouselView, top, left, arrayList);
        layoutView(verticalListView, top, (int) (left - dp1), arrayList);
        layoutView(botListTemplateView, top, left, arrayList);
        layoutView(botPieChartView, top, left, arrayList);
        layoutView(tableView, top, left, arrayList);
        layoutView(lineChartView, top, left, arrayList);
        layoutView(contactInfoView, top, left, arrayList);

        left = bubbleTextMediaLayout.getLeft();
        top = Collections.max(arrayList);
        LayoutUtils.layoutChild(cpvSenderImage, left, top);
        if (cpvSenderImage.getMeasuredWidth() > 0) {
            left = cpvSenderImage.getRight() + (int) (9 * dp1);
            top = top + (int) (1 * dp1);
        }

        LayoutUtils.layoutChild(timeStampsTextView, left, top);
        LayoutUtils.layoutChild(timeLineView, 0, top);

        botCarouselView.bringToFront();
        initializeBubbleDimensionalParametersPhase2(); //Initialize paramters, now that its layed out...

    }

    private void layoutView(View view, int top, int left, ArrayList<Integer> arrayList) {
        if (view.getVisibility() == VISIBLE) {
            LayoutUtils.layoutChild(view, left, top);
            arrayList.add(view.getBottom());
        }
    }
}
