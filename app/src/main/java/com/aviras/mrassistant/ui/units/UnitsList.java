package com.aviras.mrassistant.ui.units;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
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
 * Presenter for {@link Unit}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class UnitsList extends BasePresenter implements ListPresenter<Unit>, RealmChangeListener<RealmResults<Unit>> {

    private static final UnitsList instance = new UnitsList();
    private static final String LOG_TAG = UnitsList.class.getSimpleName();

    private ListView mListView;

    public static UnitsList sharedInstance() {
        return instance;
    }

    @Override
    public void load(CharSequence searchString) {
        Log.v(LOG_TAG, "load - searchString: " + searchString);
        List<CharSequence> ftsList = FtsUtil.getAllPossibleSearchStrings(searchString);
        RealmQuery<Unit> query = mRealm.where(Unit.class);
        for (CharSequence search : ftsList) {
            query = query.contains("name", search.toString(), Case.INSENSITIVE);
        }
        RealmResults<Unit> list = query.findAllAsync();
        list.removeChangeListener(this);
        list.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        Log.v(LOG_TAG, "setView - listView: " + listView);
        mListView = listView;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_units);
    }

    @Override
    public void onChange(RealmResults<Unit> element) {
        Log.v(LOG_TAG, "onChange - element.size(): " + element.size());
        if (null != mListView) {
            mListView.setItems(element);
        }
    }

    @Override
    public void addNew(Context context) {
        Log.v(LOG_TAG, "addNew");
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.UNIT);
        context.startActivity(intent);
    }

    @Override
    public void delete(Context context, Unit item) {
        Log.v(LOG_TAG, "delete - item: " + item);
        mRealm.beginTransaction();
        item.deleteFromRealm();
        mRealm.commitTransaction();
    }

    public interface UnitsListView extends ListView<Unit> {
    }
}
