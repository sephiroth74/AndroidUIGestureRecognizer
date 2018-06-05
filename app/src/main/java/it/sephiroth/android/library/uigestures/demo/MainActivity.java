package it.sephiroth.android.library.uigestures.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UILongPressGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIPanGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIPinchGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIRotateGestureRecognizer;
import it.sephiroth.android.library.uigestures.UISwipeGestureRecognizer;
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer;

public class MainActivity extends AppCompatActivity
    implements UIGestureRecognizer.OnActionListener, UIGestureRecognizerDelegate.Callback {

    private ViewGroup mRoot;
    private UIGestureRecognizerDelegate mDelegate;
    private DateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

        UIGestureRecognizer.setLogEnabled(BuildConfig.DEBUG);

        mDelegate = new UIGestureRecognizerDelegate(this);

        UITapGestureRecognizer recognizer1 = new UITapGestureRecognizer(this);
        recognizer1.setNumberOfTapsRequired(1);
        recognizer1.setNumberOfTouchesRequired(1);
        recognizer1.setTag("single-tap");
        recognizer1.setTapTimeout(200);
        recognizer1.setActionListener(this);

        UITapGestureRecognizer recognizer2 = new UITapGestureRecognizer(this);
        recognizer2.setTag("double-tap");
        recognizer2.setNumberOfTapsRequired(2);
        recognizer2.setNumberOfTouchesRequired(1);
        recognizer2.setActionListener(this);

        UILongPressGestureRecognizer recognizer3 = new UILongPressGestureRecognizer(this);
        recognizer3.setTag("long-press");
        recognizer3.setNumberOfTapsRequired(0);
        recognizer3.setActionListener(this);

        UILongPressGestureRecognizer recognizer4 = new UILongPressGestureRecognizer(this);
        recognizer4.setTag("long-press-2");
        recognizer4.setNumberOfTapsRequired(0);
        recognizer4.setMinimumPressDuration(4000);
        recognizer4.setAllowableMovement(500);
        recognizer4.setActionListener(this);

        UIPanGestureRecognizer recognizer5 = new UIPanGestureRecognizer(this);
        recognizer5.setTag("pan");
        recognizer5.setActionListener(this);
        recognizer5.setMinimumNumberOfTouches(1);
        recognizer5.setMaximumNumberOfTouches(5);

        UIPinchGestureRecognizer recognizer6 = new UIPinchGestureRecognizer(this);
        recognizer6.setTag("pinch");
        recognizer6.setActionListener(this);

        UIRotateGestureRecognizer recognizer7 = new UIRotateGestureRecognizer(this);
        recognizer7.setTag("rotation");
        recognizer7.setActionListener(this);

        UISwipeGestureRecognizer recognizer8 = new UISwipeGestureRecognizer(this);
        recognizer8.setTag("swipe");
        recognizer8.setActionListener(this);
        recognizer8.setNumberOfTouchesRequired(1);
        recognizer8.setDirection(UISwipeGestureRecognizer.UP | UISwipeGestureRecognizer.RIGHT);

        //recognizer3.requireFailureOf(recognizer4);

        //        mDelegate.addGestureRecognizer(recognizer7);

        // start listening for MotionEvent

        mDelegate.startListeningView(mRoot);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mRoot = findViewById(R.id.activity_main);
    }

    @Override
    public void onGestureRecognized(@NonNull final UIGestureRecognizer recognizer) {
        final String dateTime = dateFormat.format(recognizer.getLastEvent().getEventTime());
        Log.d(
            getClass().getSimpleName(),
            "[" + dateTime + "] onGestureRecognized(" + recognizer + "). state: " + recognizer.getState()
        );
        ((TextView) findViewById(R.id.text)).setText(recognizer.getState().name());
        ((TextView) findViewById(R.id.text2)).setText("[" + dateTime + "] " + recognizer.toString());
    }

    @Override
    public boolean shouldBegin(final UIGestureRecognizer recognizer) {
        return true;
    }

    @Override
    public boolean shouldRecognizeSimultaneouslyWithGestureRecognizer(
        final UIGestureRecognizer current, final UIGestureRecognizer recognizer) {
        return true;
    }

    @Override
    public boolean shouldReceiveTouch(final UIGestureRecognizer recognizer) {
        return true;
    }
}
