package com.xii.pillar.plugin;

import com.xii.pillar.domain.snapshot.ExecutionPath;
import com.xii.pillar.domain.snapshot.PNodeSnapshot;
import com.xii.pillar.domain.snapshot.PTaskSnapshot;
import com.xii.pillar.schema.PContext;

import java.util.List;

public interface ExploratoryPlugin {

    List<String> explore(ExecutionPath path, PContext context, PNodeSnapshot nodeSnapshot);
}
