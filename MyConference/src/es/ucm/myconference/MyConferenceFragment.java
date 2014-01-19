package es.ucm.myconference;

import android.app.Activity;

import com.actionbarsherlock.app.SherlockFragment;

public class MyConferenceFragment extends SherlockFragment {
	
	private MyConferenceActivity activity;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		if (!(activity instanceof MyConferenceActivity)) {
            throw new IllegalStateException(activity.getClass().getName() + " !instanceof "
                + MyConferenceActivity.class.getSimpleName());
        }

        this.activity = (MyConferenceActivity) activity;
	}
	
	protected MyConferenceActivity getInternalActivity () {
        return activity;
    }
}
