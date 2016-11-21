package it.sephiroth.android.library.uigestures.demo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import it.sephiroth.android.library.uigestures.UIGestureRecognizer;
import it.sephiroth.android.library.uigestures.UIGestureRecognizerDelegate;
import it.sephiroth.android.library.uigestures.UITapGestureRecognizer;

public class MainActivity extends AppCompatActivity implements UIGestureRecognizer.OnActionListener {

    private ViewGroup mRoot;
    private UIGestureRecognizerDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDelegate = new UIGestureRecognizerDelegate();

        UITapGestureRecognizer recognizer = new UITapGestureRecognizer(this);
        recognizer.setNumberOfTapsRequired(1);
        recognizer.setNumberOfTouchesRequired(1);
        recognizer.setTag("single-tap");
        recognizer.setActionListener(this);

        UITapGestureRecognizer recognizer2 = new UITapGestureRecognizer(this);
        recognizer2.setTag("double-tap");
        recognizer2.setNumberOfTapsRequired(2);
        recognizer2.setNumberOfTouchesRequired(1);
        recognizer2.setActionListener(this);

        recognizer.requireFailureOf(recognizer2);

        mDelegate.addGestureRecognizer(recognizer);
        mDelegate.addGestureRecognizer(recognizer2);

        mDelegate.start(mRoot);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        mRoot = (ViewGroup) findViewById(R.id.activity_main);
    }

    @Override
    public void onGestureRecognized(@NonNull final UIGestureRecognizer recognizer) {
        Log.d(getClass().getSimpleName(), "onGestureRecognized(" + recognizer + "). state: " + recognizer.getState());
    }
}
