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
                return delegate.onTouchEvent(view, motionEvent);
            }
        });
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
        mTextView.setText(recognizer.getState().name());
        mTextView2.setText(recognizer.toString());
    }
}
