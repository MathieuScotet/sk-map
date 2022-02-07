package tech.skot.libraries.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class SkMapClusterRenderer(
    context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<SKClusterMarker>,
    val clusteringInteractionSettings: SKMapVC.MapClusteringInteractionSettings
) : DefaultClusterRenderer<SKClusterMarker>(context, map, clusterManager) {





}