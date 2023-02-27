import React from 'react';
import { Route } from 'react-router-dom';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';
import Reservation from 'app/modules/reservation/Reservation';
import Account from 'app/modules/account/Account';
import { RoutePath } from 'app/components/Routes/RoutePath';

import Vehicle from './vehicle';
import Appointment from './appointment';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <>
      <ErrorBoundaryRoutes>
        <Route path={RoutePath.Reservation} element={<Reservation />} />
        <Route path={RoutePath.Account} element={<Account />} />
        <Route path="vehicle/*" element={<Vehicle />} />
        <Route path="appointment/*" element={<Appointment />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </>
  );
};
