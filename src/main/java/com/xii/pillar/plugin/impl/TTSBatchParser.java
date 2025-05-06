package com.xii.pillar.plugin.impl;

import com.xii.pillar.domain.snapshot.PNodeSnapshot;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.plugin.ExploratoryPlugin;
import com.xii.pillar.schema.PContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service("TTSBatchParser")
public class TTSBatchParser implements ExploratoryPlugin {


    @Override
    public List<String> explore(PContext context, PNodeSnapshot nodeSnapshot, PTaskSnapshot taskSnapshot) {
        return Collections.emptyList();
    }
}
