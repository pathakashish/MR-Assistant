package com.aviras.mrassistant.ui.doctors;

import android.content.Context;
import android.content.Intent;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Doctor;
import com.aviras.mrassistant.logger.Log;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListPresenter;
import com.aviras.mrassistant.ui.lists.ListView;
import com.aviras.mrassistant.ui.utils.FtsUtil;

import java.util.List;

import io.realm.Case;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Presenter for {@link Doctor}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class DoctorsList extends BasePresenter implements ListPresenter<Doctor>, RealmChangeListener<RealmResults<Doctor>> {

    private static final DoctorsList instance = new DoctorsList();
    private static final String LOG_TAG = DoctorsList.class.getSimpleName();

    private ListView mListView;
    private RealmResults<Doctor> mCurrentList;

    public static DoctorsList sharedInstance() {
        return instance;
    }

    @Override
    public void load(CharSequence searchString) {
        Log.v(LOG_TAG, "load - searchString: " + searchString);
        List<CharSequence> ftsList = FtsUtil.getAllPossibleSearchStrings(searchString);
        RealmQuery<Doctor> query = mRealm.where(Doctor.class);
        for (CharSequence search : ftsList) {
            query = query.contains("name", search.toString(), Case.INSENSITIVE)
                    .or()
                    .contains("contactNumber", search.toString(), Case.INSENSITIVE)
                    .or()
                    .contains("address", search.toString(), Case.INSENSITIVE)
                    .or()
                    .contains("notes", search.toString(), Case.INSENSITIVE);
            ;
        }
        if (null != mCurrentList) {
            mCurrentList.removeChangeListener(this);
        }
        mCurrentList = query.findAllAsync();
        mCurrentList.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        Log.v(LOG_TAG, "setView - listView: " + listView);
        mListView = listView;
    }

    @Override
    public void addNew(Context context) {
        Log.v(LOG_TAG, "addNew");
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.DOCTOR);
        context.startActivity(intent);
    }

    @Override
    public void delete(Context context, Doctor item) {
        Log.v(LOG_TAG, "delete - " + item);
        mRealm.beginTransaction();
        item.deleteFromRealm();
        mRealm.commitTransaction();
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_doctors);
    }

    @Override
    public void onChange(RealmResults<Doctor> element) {
        Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
        if (null != mListView) {
            mListView.setItems(element);
        }
    }

    public interface DoctorsListView extends ListView<Doctor> {
    }
}
