import React from 'react';
import { Route } from 'react-router-dom';
import Loadable from 'react-loadable';

import { LoginRedirect } from 'app/modules/login/login-redirect';
import { Logout } from 'app/modules/login/logout';
import Home from 'app/modules/home/home';
import EntitiesRoutes from 'app/entities/routes';
import { PrivateRoute } from 'app/shared/auth/private-route';
import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import PageNotFound from 'app/shared/error/page-not-found';
import { AUTHORITIES } from 'app/config/constants';
import MainLayout from 'app/shared/layout/main/MainLayout';
import { RoutePath } from 'app/components/Routes/RoutePath';
import Container from '@mui/material/Container';
import FullContainer from 'app/shared/styled/FullContainer';

const loading = <div>loading ...</div>;

const Admin = Loadable({
  loader: () => import(/* webpackChunkName: "administration" */ 'app/modules/administration'),
  loading: () => loading,
});

const AppRoutes = () => {
  return (
    <>
      <ErrorBoundaryRoutes>
        <Route path={RoutePath.Home} element={<Home />} />
        <Route path={RoutePath.Logout} element={<Logout />} />
        <Route
          path={RoutePath.AdminWildcard}
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.ADMIN]}>
              <Admin />
            </PrivateRoute>
          }
        />
        <Route path={RoutePath.Login} element={<LoginRedirect />} />
        <Route
          path={RoutePath.Wildcard}
          element={
            <PrivateRoute hasAnyAuthorities={[AUTHORITIES.USER]}>
              <EntitiesRoutes />
            </PrivateRoute>
          }
        />
        <Route path={RoutePath.Wildcard} element={<PageNotFound />} />
      </ErrorBoundaryRoutes>
    </>
  );
};

export default AppRoutes;
