package com.example.myapplication.ui;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.database.MemoDatabase;
import com.example.myapplication.model.AMemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class MemoViewModel extends ViewModel {
    public static final String DATA_UNFINISHED = "unfinished";
    public static final String DATA_FINISHED = "finished";
    public static final String DATA_RECYCLABLE = "recyclable";
    private final MutableLiveData<ArrayList<AMemo>> unfinishedMemoList;
    private final MutableLiveData<ArrayList<AMemo>> finishedMemoList;
    private final MutableLiveData<ArrayList<AMemo>> recyclableMemoList;
    private ArrayList<AMemo> selectedMemos;
    private final HashMap<String, ArrayList<AMemo>> selectedMemosMap;
    private final HashMap<String, MutableLiveData<ArrayList<AMemo>>> liveDataMap;
    private final HashMap<String, ArrayList<AMemo>> originalMemosMap;

    private String currentDataType = DATA_UNFINISHED;

    public MemoViewModel() {
        unfinishedMemoList = new MutableLiveData<>(new ArrayList<>());
        finishedMemoList = new MutableLiveData<>(new ArrayList<>());
        recyclableMemoList = new MutableLiveData<>(new ArrayList<>());
        selectedMemos = new ArrayList<>();

        selectedMemosMap = new HashMap<>();
        selectedMemosMap.put(DATA_UNFINISHED, new ArrayList<>());
        selectedMemosMap.put(DATA_FINISHED, new ArrayList<>());
        selectedMemosMap.put(DATA_RECYCLABLE, new ArrayList<>());

        originalMemosMap = new HashMap<>();
        originalMemosMap.put(DATA_UNFINISHED, new ArrayList<>());
        originalMemosMap.put(DATA_FINISHED, new ArrayList<>());
        originalMemosMap.put(DATA_RECYCLABLE, new ArrayList<>());

        liveDataMap = new HashMap<>();
        liveDataMap.put(DATA_UNFINISHED, unfinishedMemoList);
        liveDataMap.put(DATA_FINISHED, finishedMemoList);
        liveDataMap.put(DATA_RECYCLABLE, recyclableMemoList);
    }

    public String getCurrentDataType() {
        return currentDataType;
    }

    public MutableLiveData<ArrayList<AMemo>> getUnfinishedMemoList() {
        return unfinishedMemoList;
    }

    public MutableLiveData<ArrayList<AMemo>> getFinishedMemoList() {
        return finishedMemoList;
    }

    public MutableLiveData<ArrayList<AMemo>> getRecyclableMemoList() {
        return recyclableMemoList;
    }

    public ArrayList<AMemo> getSelectedMemos() {
        return selectedMemos;
    }

    /**
     * Sets the data type of the current operation.
     *
     * @param type String in [DATA_UNFINISHED,DATA_FINISHED,DATA_RECYCLABLE]
     */
    public void setCurrentDataType(String type) {
        if (!selectedMemosMap.containsKey(type)) {
            return;
        }
        ArrayList<AMemo> originalMemoList = originalMemosMap.get(currentDataType);
        assert originalMemoList != null;
        //cancel current search
        if (originalMemoList.size() > 0) {
            cancelSearch();
        }
        selectedMemosMap.put(currentDataType, new ArrayList<>(selectedMemos));
        currentDataType = type;
        selectedMemos = new ArrayList<>(Objects.requireNonNull(selectedMemosMap.get(type)));
    }

    /**
     * add new memo
     *
     * @param title   String
     * @param content String
     */
    public void addMemo(String title, String content) {
        ArrayList<AMemo> memos = unfinishedMemoList.getValue();
        AMemo newMemo = new AMemo();
        newMemo.setTitle(title);
        newMemo.setContent(content);
        assert memos != null;
        memos.add(0, newMemo);
        unfinishedMemoList.postValue(memos);
    }

    /**
     * update memo
     *
     * @param title   String
     * @param content String
     */
    public void updateMemo(AMemo memo, String title, String content) {
        ArrayList<AMemo> memos = unfinishedMemoList.getValue();
        assert memos != null;
        for (int i = 0; i < memos.size(); i++) {
            AMemo m = memos.get(i);
            if (m == memo) {
                m.setTitle(title);
                m.setContent(content);
                m.setModDate(System.currentTimeMillis());
                unfinishedMemoList.postValue(memos);
            }
        }
    }

    /**
     * delete memos
     */
    public void deleteMemos() {
        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(currentDataType);
        assert liveData != null;
        ArrayList<AMemo> memos = liveData.getValue();
        assert memos != null;
        memos.removeAll(selectedMemos);
        selectedMemos.clear();
        liveData.postValue(memos);
    }

    /**
     * remove selected memos
     *
     * @param liveData
     */
    private void removeSelected(MutableLiveData<ArrayList<AMemo>> liveData,
                                ArrayList<AMemo> removable) {
        ArrayList<AMemo> memos = liveData.getValue();
        assert memos != null;
        memos.removeAll(removable);
        liveData.postValue(memos);
    }

    /**
     * put memos to recycler bin
     */
    public void recycleMemos() {
        ArrayList<AMemo> recyclerBin = recyclableMemoList.getValue();
        assert recyclerBin != null;
        recyclerBin.addAll(0, selectedMemos);

        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(currentDataType);
        assert liveData != null;
        removeSelected(liveData, selectedMemos);

        selectedMemos.clear();
        recyclableMemoList.postValue(recyclerBin);
    }

    /**
     * restore memo from recycler bin
     */
    public void restoreRecyclableMemos() {
        ArrayList<AMemo> recyclerBin = recyclableMemoList.getValue();
        assert recyclerBin != null;
        MutableLiveData<ArrayList<AMemo>> liveDataFinished = liveDataMap.get(DATA_FINISHED);
        assert liveDataFinished != null;
        ArrayList<AMemo> finishedMemos = liveDataFinished.getValue();

        MutableLiveData<ArrayList<AMemo>> liveDataUnfinished = liveDataMap.get(DATA_UNFINISHED);
        assert liveDataUnfinished != null;
        ArrayList<AMemo> unfinishedMemos = liveDataUnfinished.getValue();

        for (AMemo memo : selectedMemos) {
            if (memo.isFinished()) {
                assert finishedMemos != null;
                finishedMemos.add(0, memo);
            } else {
                assert unfinishedMemos != null;
                unfinishedMemos.add(0, memo);
            }
        }
        removeSelected(recyclableMemoList, selectedMemos);
        selectedMemos.clear();
    }

    /**
     * select a memo
     *
     * @param memo AMemo
     */
    public void addMemoToSelected(AMemo memo) {
        selectedMemos.add(memo);
    }

    /**
     * unselect a memo
     *
     * @param memo AMemo
     */
    public void removeMemoFromSelected(AMemo memo) {
        selectedMemos.remove(memo);
    }

    /**
     * set a memo as finished
     *
     * @param memo
     */
    public void finishMemo(AMemo memo) {
        memo.setFinished(true);
        //add to finished
        ArrayList<AMemo> finishedMemos = finishedMemoList.getValue();
        assert finishedMemos != null;
        finishedMemos.add(0, memo);
        finishedMemoList.postValue(finishedMemos);

        //remove from unfinished
        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(DATA_UNFINISHED);
        assert liveData != null;
        ArrayList<AMemo> finished = new ArrayList<>();
        finished.add(memo);
        removeSelected(liveData, finished);
    }

    /**
     * set a memo as unfinished
     *
     * @param memo
     */
    public void unFinishMemo(AMemo memo) {
        memo.setFinished(false);
        //add to unfinished
        ArrayList<AMemo> unfinishedMemos = unfinishedMemoList.getValue();
        assert unfinishedMemos != null;
        unfinishedMemos.add(0, memo);
        unfinishedMemoList.postValue(unfinishedMemos);

        //remove from finished
        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(DATA_FINISHED);
        assert liveData != null;
        ArrayList<AMemo> unfinished = new ArrayList<>();
        unfinished.add(memo);
        removeSelected(liveData, unfinished);
    }

    /**
     * Set unfinished note to top note.
     *
     * @param memo object, a given note.
     */
    public void setOnTop(AMemo memo) {
        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(currentDataType);
        assert liveData != null;
        ArrayList<AMemo> memos = liveData.getValue();
        assert memos != null;
        memos.remove(memo);
        memos.add(0, memo);
        liveData.postValue(memos);
    }

    /**
     * Search Note by title.
     *
     * @param title String, a given title.
     */
    public void searchMemos(String title) {
        //empty key words
        if (title.length() == 0) {
            return;
        }
        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(currentDataType);
        assert liveData != null;
        ArrayList<AMemo> memos = liveData.getValue();
        assert memos != null;
        //there is no memo to search
        if (memos.size() == 0) {
            return;
        }
        ArrayList<AMemo> originalMemoList = originalMemosMap.get(currentDataType);
        //save original memos
        assert originalMemoList != null;
        if(originalMemoList.size()==0){
            originalMemoList.addAll(memos);
            originalMemosMap.put(currentDataType, originalMemoList);
        }
        //search results
        ArrayList<AMemo> searchedMemos = (ArrayList<AMemo>) memos
                .stream().filter(aMemo -> aMemo.getTitle().toLowerCase(Locale.ROOT)
                        .contains(title)).collect(Collectors.toList());
        liveData.postValue(searchedMemos);
    }

    /**
     * cancel search
     */
    public void cancelSearch() {
        ArrayList<AMemo> originalMemoList = originalMemosMap.get(currentDataType);
        assert originalMemoList != null;
        //not a search
        if (originalMemoList.size() == 0) {
            return;
        }
        MutableLiveData<ArrayList<AMemo>> liveData = liveDataMap.get(currentDataType);
        assert liveData != null;

        liveData.postValue(new ArrayList<>(originalMemoList));
        originalMemoList.clear();
        originalMemosMap.put(currentDataType, originalMemoList);
    }
}
