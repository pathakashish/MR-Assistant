package com.aviras.mrassistant.ui.units;

import android.content.Context;
import android.content.Intent;

import com.aviras.mrassistant.R;
import com.aviras.mrassistant.data.models.Unit;
import com.aviras.mrassistant.ui.BasePresenter;
import com.aviras.mrassistant.ui.Presenter;
import com.aviras.mrassistant.ui.editors.EditorActivity;
import com.aviras.mrassistant.ui.lists.ListPresenter;
import com.aviras.mrassistant.ui.lists.ListView;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;

/**
 * Presenter for {@link Unit}s list
 * <p/>
 * Created by ashish on 14/6/16.
 */
public class UnitsList extends BasePresenter implements ListPresenter<Unit>, RealmChangeListener<RealmResults<Unit>> {

    private static final UnitsList instance = new UnitsList();

    private ListView mListView;

    public static UnitsList sharedInstance() {
        return instance;
    }

    @Override
    public void load() {
        RealmResults<Unit> list = mRealm.where(Unit.class).findAllAsync();
        list.addChangeListener(this);
    }

    @Override
    public void setView(ListView listView) {
        mListView = listView;
    }

    @Override
    public CharSequence getTitle(Context context) {
        return context.getString(R.string.title_activity_units);
    }

    @Override
    public void onChange(RealmResults<Unit> element) {
        if (null != mListView) {
            mListView.setItems(element);
        }
    }

    @Override
    public void addNew(Context context) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EditorActivity.EXTRA_EDITING_FOR, Presenter.UNIT);
        context.startActivity(intent);
    }

    @Override
    public void delete(Context context, int id) {

    }

    public interface UnitsListView extends ListView<Unit> {
    }
}
