package com.android.terminalbox.devservice.uhf;

import com.android.terminalbox.bean.ConstTagStatus;
import com.android.terminalbox.bean.UhfTagStatus;
import com.android.terminalbox.uhf.UhfTag;
import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import java.util.stream.Collectors;

public class LocalTags {
    private static final String TAG = "LocalTags";
    private static LocalTags localTags;
    private static Set<String> cacheEpcs=new HashSet<>();
    public static LocalTags getInstance() {
        if (localTags == null) {
            synchronized (LocalTags.class) {
                if (localTags == null) {
                    localTags = new LocalTags();
                    loadEpcsFromDb();
                }
            }
        }
        return localTags;
    }
    private static void loadEpcsFromDb() {
//        UhfServer uhfServer = UhfServer.getInstance();
//        uhfServer.startReadTags( new EsimUhfListener() {
//            @Override
//            public void onTagRead(List<UhfTag> tags) {
////                cacheEpcs = tags.stream().map(UhfTag::getEpc).collect(Collectors.toSet());
//                cacheEpcs = Stream.of(tags).map(UhfTag::getEpc).collect(Collectors.toSet());
//            }
//        });
    }
    public static List<UhfTagStatus> changeTags(Set<String> readEpcs){
//        Set<String> in=readEpcs.stream().filter(epc->!cacheEpcs.contains(epc)).collect(Collectors.toSet());
//        Set<String> out=cacheEpcs.stream().filter(epc->!readEpcs.contains(epc)).collect(Collectors.toSet());
//        Set<String> unChange=readEpcs.stream().filter(epc->cacheEpcs.contains(epc)).collect(Collectors.toSet());

        Set<String> in=Stream.of(readEpcs).filter(epc->!cacheEpcs.contains(epc)).collect(Collectors.toSet());
        Set<String> out=Stream.of(cacheEpcs).filter(epc->!readEpcs.contains(epc)).collect(Collectors.toSet());
        Set<String> unChange=Stream.of(readEpcs).filter(epc->cacheEpcs.contains(epc)).collect(Collectors.toSet());
        cacheEpcs.clear();
        cacheEpcs.addAll(readEpcs);

        List<UhfTagStatus> showTags=new ArrayList<>();
        showTags.addAll(Stream.of(in).map(epc-> new UhfTagStatus(ConstTagStatus.TAG_IN,epc,"暂不考虑名称")).collect(Collectors.toSet()));
        showTags.addAll(Stream.of(out).map(epc-> new UhfTagStatus(ConstTagStatus.TAG_OUT,epc,"暂不考虑名称")).collect(Collectors.toSet()));
        showTags.addAll(Stream.of(unChange).map(epc-> new UhfTagStatus(ConstTagStatus.TAG_BOX,epc,"暂不考虑名称")).collect(Collectors.toSet()));
        return showTags;
    }
    public static List<UhfTagStatus> changeTags(Collection<UhfTag> readTags){
        Set<String> readEpcs=Stream.of(readTags).map(UhfTag::getEpc).collect(Collectors.toSet());
        return changeTags(readEpcs);
    }
    public List<UhfTagStatus>  getCacheTags() {
        List<UhfTagStatus> showTags=new ArrayList<>();
        showTags.addAll(Stream.of(cacheEpcs).map(epc-> new UhfTagStatus(ConstTagStatus.TAG_BOX,epc,"暂不考虑名称")).collect(Collectors.toSet()));
        return showTags;
    }
    public Set<String> getCacheEpcs() {
        return cacheEpcs;
    }

    public void setCacheEpcs(Set<String> cacheEpcs) {
        LocalTags.cacheEpcs = cacheEpcs;
    }
}
