package it.sephiroth.android.library.uigestures.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;

public class BaseTest extends AppCompatActivity implements UIGestureRecognizer.OnActionListener {

    public UIGestureRecognizerDelegate delegate;
    private TextView mTextView;
    private TextView mTextView2;
    private UIGestureRecognizer.State mCurrentState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        delegate = new UIGestureRecognizerDelegate(null);
        findViewById(R.id.activity_main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent motionEvent) {
                mTextView2.setText(actionToString(motionEvent.getActionMasked()));
                return delegate.onTouchEvent(view, motionEvent);
            }
        });
    }

    private static String actionToString(final int action) {
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                return "ACTION_DOWN";
            case MotionEvent.ACTION_UP:
                return "ACTION_UP";
            case MotionEvent.ACTION_CANCEL:
                return "ACTION_CANCEL";
            case MotionEvent.ACTION_MOVE:
                return "ACTION_MOVE";
            case MotionEvent.ACTION_POINTER_DOWN:
                return "ACTION_POINTER_DOWN";
            case MotionEvent.ACTION_POINTER_UP:
                return "ACTION_POINTER_UP";
            default:
                return "ACTION_OTHER";
        }
    }

    public UIGestureRecognizer.State getCurrentState() {
        return mCurrentState;
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        mTextView = ((TextView) findViewById(R.id.text));
        mTextView2 = ((TextView) findViewById(R.id.text2));
    }

    @Override
    public void onGestureRecognized(@NonNull final UIGestureRecognizer recognizer) {
        mCurrentState = recognizer.getState();
        mTextView.setText(recognizer.getTag() + ": " + recognizer.getState().name());
    }
}
