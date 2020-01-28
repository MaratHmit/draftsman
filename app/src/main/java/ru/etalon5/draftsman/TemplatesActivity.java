package ru.etalon5.draftsman;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

public class TemplatesActivity extends AppCompatActivity {

    private static final int IDM_RENAME = 0;
    private static final int IDM_DELETE = 1;
    private static final int RESULT_DRAW_OK = 1;
    private ArrayList<DataDraft> mTemplatesDrafts;
    private boolean mIsSelectedMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_templates);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent i = getIntent();
        mIsSelectedMode = i.getBooleanExtra("isSelected", true);
        ListView listView = (ListView)findViewById(R.id.listViewNames);
        mTemplatesDrafts = new ArrayList<>();
        TemplatesListAdapter adapter = new TemplatesListAdapter(this, mTemplatesDrafts);
        loadData();
        listView.setAdapter(adapter);
        setPagesDrafts();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position,
                                    long id) {
                onClickDraftName(position);
            }
        });
        registerForContextMenu(listView);
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_template, menu);
        if (!mIsSelectedMode)
            menu.getItem(1).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_select) {
            String draft = null;
            for (int i = 0; i < mTemplatesDrafts.size(); ++i)
                if (mTemplatesDrafts.get(i).isSelected())
                    draft = mTemplatesDrafts.get(i).getDraft();
            Intent data = new Intent();
            data.putExtra("draft", draft);
            setResult(RESULT_OK, data);
            finish();
            return true;
        }
        if (id == R.id.action_add_template)
            onAddTemplate();

        return super.onOptionsItemSelected(item);
    }

    private void onAddTemplate() {
        Intent i = new Intent(this, DraftActivity.class);
        startActivityForResult(i, RESULT_DRAW_OK);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewNames) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            onClickDraftName(info.position);
            menu.setHeaderTitle(mTemplatesDrafts.get(info.position).getName());
            String[] menuItems = getResources().getStringArray(R.array.menu_item_template);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            case IDM_RENAME: {
                renameTemplate();
                return true;
            }
            case IDM_DELETE: {
                deleteTemplate();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_DRAW_OK) {
                if (data.hasExtra("draft") && data.hasExtra("draftName"))
                    addTemplate(data.getStringExtra("draftName"), data.getStringExtra("draft"));
            }
        }
    }

    private void addTemplate(String draftName, String draft) {
        if (draft == null || draft.isEmpty())
            return;

        if (StoreMeasures.saveDraftAsTemplate(draft, draftName)) {
            loadData();
            setPagesDrafts();
            ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperTemplates);
            for (int i = 0; i < mTemplatesDrafts.size(); ++i) {
                mTemplatesDrafts.get(i).setIsSelected(false);
                if (mTemplatesDrafts.get(i).getDraft().equals(draft)) {
                    mTemplatesDrafts.get(i).setIsSelected(true);
                    break;
                }
                flipper.showNext();
            }
            ListView listView = (ListView)findViewById(R.id.listViewNames);
            listView.invalidateViews();
        }
    }

    private void deleteTemplate() {
        int pos = 0;
        for (int i = 0; i < mTemplatesDrafts.size(); ++i)
            if (mTemplatesDrafts.get(i).isSelected()) {
                pos = i;
                break;
            }
        final DataDraft dataDraft = mTemplatesDrafts.get(pos);

        AlertDialog.Builder ad;
        String title = getResources().getString(R.string.aply_deleted);
        String message = getResources().getString(R.string.to_temove_template).concat(" " + dataDraft.getName() + "?");
        String buttonYesString = getResources().getString(R.string.btn_yes);
        String buttonNoString = getResources().getString(R.string.btn_no);

        ad = new AlertDialog.Builder(this);
        ad.setTitle(title);
        ad.setMessage(message);

        ad.setPositiveButton(buttonYesString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                int pos = -1;
                for (int i = 0; i < mTemplatesDrafts.size(); ++i)
                    if (mTemplatesDrafts.get(i).isSelected()) {
                        pos = i;
                        break;
                    }
                if (StoreMeasures.removeTemplate(dataDraft)) {
                    mTemplatesDrafts.remove(dataDraft);
                    ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperTemplates);
                    ListView listView = (ListView)findViewById(R.id.listViewNames);
                    if (pos >= 0)
                        flipper.removeViewAt(pos);
                    if (pos >= mTemplatesDrafts.size())
                        pos--;
                    if (pos >= 0)
                        mTemplatesDrafts.get(pos).setIsSelected(true);
                    listView.invalidateViews();
                }
            }
        });
        ad.setNegativeButton(buttonNoString, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(false);
        ad.show();
    }

    private void renameTemplate() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(R.string.rename_template);
        alert.setMessage(R.string.name_template);

        int pos = 0;
        for (int i = 0; i < mTemplatesDrafts.size(); ++i)
            if (mTemplatesDrafts.get(i).isSelected()) {
                pos = i;
                break;
            }

        final DataDraft dataDraft = mTemplatesDrafts.get(pos);
        final EditText input = new EditText(this);
        input.setText(dataDraft.getName());
        alert.setView(input);

        alert.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!input.getText().toString().trim().isEmpty())
                    dataDraft.setName(input.getText().toString().trim());
                StoreMeasures.renameTemplate(dataDraft);
            }
        });

        alert.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    private void onClickDraftName(int position) {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperTemplates);
        ListView listView = (ListView)findViewById(R.id.listViewNames);
        DataDraft dataDraft = mTemplatesDrafts.get(position);
        int oldPosition = 0;
        for (int i = 0; i < mTemplatesDrafts.size(); ++i) {
            if (mTemplatesDrafts.get(i).isSelected())
                oldPosition = i;
            mTemplatesDrafts.get(i).setIsSelected(false);
        }
        dataDraft.setIsSelected(true);
        if (oldPosition < position)
            for (int i = oldPosition; i < position; ++i)
                flipper.showNext();
        else
            for (int i = oldPosition; i > position; --i)
                flipper.showPrevious();

        listView.invalidateViews();
    }

    public void setSelectedItem(String key) {
        for (int i = 0; i < mTemplatesDrafts.size(); ++i)
            mTemplatesDrafts.get(i).setIsSelected(mTemplatesDrafts.get(i).getKey() == key);
        ListView listView = (ListView)findViewById(R.id.listViewNames);
        listView.invalidateViews();
    }

    private void loadData() {
        StoreMeasures.fetchTemplatesDrafts(mTemplatesDrafts);
    }

    private class TemplatesListAdapter extends ArrayAdapter<DataDraft> {

        private Activity mActivity;

        public TemplatesListAdapter(Activity activity, ArrayList<DataDraft> list) {
            super(activity, 0, list);
            mActivity = activity;
        }

        @SuppressLint("InflateParams") @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = mActivity.getLayoutInflater().inflate(android.R.layout.simple_list_item_1, null);
            }

            DataDraft draft = getItem(position);
            TextView titleTextView = (TextView)convertView;
            titleTextView.setText(draft.getName());

            if (draft.isSelected())
                titleTextView.setBackgroundColor(Color.rgb(255, 64, 129));
            else titleTextView.setBackgroundColor(Color.rgb(255, 255, 255));

            return convertView;
        }
    }

    private void setPagesDrafts() {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperTemplates);
        flipper.removeAllViews();
        for (DataDraft draft: mTemplatesDrafts)
            addPageDraft(draft);

    }

    private void addPageDraft(DataDraft dataDraft, int index) {
        ViewFlipper flipper = (ViewFlipper) findViewById(R.id.viewFlipperTemplates);
        DrawScene scene = new DrawScene(this, dataDraft, 80, true);
        scene.setFlipper(flipper);
        if (index >= 0)
            flipper.addView(scene, index);
        else flipper.addView(scene);
    }

    private void addPageDraft(DataDraft dataDraft) {
        addPageDraft(dataDraft, -1);
    }
}
