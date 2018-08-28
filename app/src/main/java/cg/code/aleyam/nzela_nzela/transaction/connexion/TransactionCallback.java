package cg.code.aleyam.nzela_nzela.transaction.connexion;

import java.util.HashMap;

public interface TransactionCallback {

    void TransactionSucceded(String ok_message, HashMap dataKey);
    void TransactionFailed(String error_message, boolean tryOption);

}

//un log a gerer apres.

//E/AndroidRuntime: FATAL EXCEPTION: main
//        Process: cg.code.aleyam.nzela_nzela, PID: 3972
//        java.lang.IndexOutOfBoundsException: Invalid index 0, size is 0
//        at java.util.ArrayList.throwIndexOutOfBoundsException(ArrayList.java:255)
//        at java.util.ArrayList.get(ArrayList.java:308)
//        at android.support.v4.view.ViewPager.performDrag(ViewPager.java:2318)
//        at android.support.v4.view.ViewPager.onTouchEvent(ViewPager.java:2236)
//        at android.view.View.dispatchTouchEvent(View.java:8070)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2428)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2151)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2434)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2166)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2434)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2166)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2434)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2166)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2434)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2166)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2434)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2166)
//        at android.view.ViewGroup.dispatchTransformedTouchEvent(ViewGroup.java:2434)
//        at android.view.ViewGroup.dispatchTouchEvent(ViewGroup.java:2166)
//        at com.android.internal.policy.impl.PhoneWindow$DecorView.superDispatchTouchEvent(PhoneWindow.java:2311)
//        at com.android.internal.policy.impl.PhoneWindow.superDispatchTouchEvent(PhoneWindow.java:1606)
//        at android.app.Activity.dispatchTouchEvent(Activity.java:2617)
//        at android.support.v7.view.WindowCallbackWrapper.dispatchTouchEvent(WindowCallbackWrapper.java:68)
//        at android.support.v7.view.WindowCallbackWrapper.dispatchTouchEvent(WindowCallbackWrapper.java:68)
//        at com.android.internal.policy.impl.PhoneWindow$DecorView.dispatchTouchEvent(PhoneWindow.java:2259)
//        at android.view.View.dispatchPointerEvent(View.java:8265)
//        at android.view.ViewRootImpl$ViewPostImeInputStage.processPointerEvent(ViewRootImpl.java:4693)
//        at android.view.ViewRootImpl$ViewPostImeInputStage.onProcess(ViewRootImpl.java:4554)
//        at android.view.ViewRootImpl$InputStage.deliver(ViewRootImpl.java:4112)
//        at android.view.ViewRootImpl$InputStage.onDeliverToNext(ViewRootImpl.java:4166)
//        at android.view.ViewRootImpl$InputStage.forward(ViewRootImpl.java:4135)
//        at android.view.ViewRootImpl$AsyncInputStage.forward(ViewRootImpl.java:4246)
//        at android.view.ViewRootImpl$InputStage.apply(ViewRootImpl.java:4143)
//        at android.view.ViewRootImpl$AsyncInputStage.apply(ViewRootImpl.java:4303)
//        at android.view.ViewRootImpl$InputStage.deliver(ViewRootImpl.java:4112)
//        at android.view.ViewRootImpl$InputStage.onDeliverToNext(ViewRootImpl.java:4166)
//        at android.view.ViewRootImpl$InputStage.forward(ViewRootImpl.java:4135)
//        at android.view.ViewRootImpl$InputStage.apply(ViewRootImpl.java:4143)
//        at android.view.ViewRootImpl$InputStage.deliver(ViewRootImpl.java:4112)
//        at android.view.ViewRootImpl.deliverInputEvent(ViewRootImpl.java:6489)
//        at android.view.ViewRootImpl.doProcessInputEvents(ViewRootImpl.java:6406)
//        at android.view.ViewRootImpl.enqueueInputEvent(ViewRootImpl.java:6377)
//        at android.view.ViewRootImpl.enqueueInputEvent(ViewRootImpl.java:6342)
//        at android.view.ViewRootImpl$WindowInputEventReceiver.onInputEvent(ViewRootImpl.java:6569)
//        at android.view.InputEventReceiver.dispatchInputEvent(InputEventReceiver.java:185)
//        at android.view.InputEventReceiver.nativeConsumeBatchedInputEvents(Native Method)
//        at android.view.InputEventReceiver.consumeBatchedInputEvents(InputEventReceiver.java:176)