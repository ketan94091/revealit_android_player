package com.Revealit.Interfaces;

import java.util.ArrayList;

public interface RemoveListenHistory {

    public void removeListenHistory(boolean isFromLiveMode);
    public void getSelectedIds(ArrayList<Integer> selectedIdsList);
    public void isSingleTimeStampDeleted(boolean isTimeStampDeleted);
}
