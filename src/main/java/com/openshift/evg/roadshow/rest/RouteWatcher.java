package com.openshift.evg.roadshow.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import io.fabric8.kubernetes.client.Watch;
import io.fabric8.openshift.api.model.Route;

@Component
public class RouteWatcher extends AbstractResourceWatcher<Route> {
  private static final Logger logger = LoggerFactory.getLogger(ServiceWatcher.class);

  private static final String PARKSMAP_BACKEND_LABEL = "type=parksmap-backend";

  @Override
  protected List<Route> listWatchedResources() {
    return getOpenShiftClient().routes().inNamespace(getNamespace()).withLabel(PARKSMAP_BACKEND_LABEL).list()
        .getItems();
  }

  @Override
  protected Watch doInit() {
    return getOpenShiftClient().routes().inNamespace(getNamespace()).withLabel(PARKSMAP_BACKEND_LABEL).watch(this);
  }

  @Override
  protected String getUrl(String routeName) {
    List<Route> routes = getOpenShiftClient().routes().inNamespace(getNamespace()).withLabel(PARKSMAP_BACKEND_LABEL)
        .withField("metadata.name", routeName).list().getItems();
    if (routes.isEmpty()) {
      return null;
    }

    Route route = routes.get(0);
    String routeUrl = "";
    try {
      String protocol = "http://";
      if((route.getSpec().getTls()!=null)&&(route.getSpec().getTls().getTermination()!=null)){
        protocol = "https://";
      }
      routeUrl = protocol + route.getSpec().getHost();
    } catch (Exception e) {
      logger.error("Route {} does not have a port assigned", routeName);
    }

    List<Route> rList = getOpenShiftClient().routes().inNamespace(getNamespace()).list().getItems();
    
    logger.info("[INFO] Computed route URL: {}", routeUrl);
    logger.info("Testing - Route- made by kevin dalling");
    logger.info("Size= {}",rList.size());
    logger.info("info #1 - {}",rList.get(0).getSpec().getHost());
    logger.info("info #2 - {}",rList.get(1).getSpec().getHost());
    logger.info("info #3 - {}",rList.get(2).getSpec().getHost());
    logger.info("info #4 - {}",rList.get(3).getSpec().getHost());
    
    return routeUrl;
  }
}
