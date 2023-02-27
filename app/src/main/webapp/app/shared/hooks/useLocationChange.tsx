import { Reducer, useEffect, useReducer } from 'react';
import { matchPath, useLocation } from 'react-router-dom';
import { RoutePath } from 'app/components/Routes/RoutePath';

export const locationRouteReducer: Reducer<RoutePath, string> = (state, pathname) => {
  if (matchPath(RoutePath.Home, pathname)) {
    return RoutePath.Home;
  } else if (matchPath(RoutePath.Reservation, pathname)) {
    return RoutePath.Reservation;
  } else {
    return RoutePath.Home;
  }
};

export const useLocationRoute = () => {
  const { pathname } = useLocation();
  const [route, dispatch] = useReducer<Reducer<RoutePath, string>>(locationRouteReducer, RoutePath.Home);

  useEffect(() => {
    dispatch(pathname);
  }, [pathname]);

  return { route };
};

export const useRouteActive = (route: RoutePath | string) => {
  const { pathname } = useLocation();
  return matchPath(route, pathname)
}
