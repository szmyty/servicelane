import 'react-toastify/dist/ReactToastify.css';
import './app.scss';
import 'app/config/dayjs';
import React, { useEffect } from 'react';
import { Card } from 'reactstrap';
import { ToastContainer, toast } from 'react-toastify';
import { useAppDispatch, useAppSelector } from 'app/config/store';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { AUTHORITIES } from 'app/config/constants';
import AppRoutes from 'app/routes';
import AppBar from 'app/components/AppBar';
import FullPaper from './shared/styled/FullPaper/FullPaper';
import Box from '@mui/material/Box';
import useDimensions from 'react-cool-dimensions';
import Paper from '@mui/material/Paper/Paper';

export const App = () => {
  const dispatch = useAppDispatch();
  const { observe, height } = useDimensions<HTMLDivElement>({
    onResize: ({ observe, unobserve }) => {
      unobserve(); // To stop observing the current target element
      observe(); // To re-start observing the current target element
    },
  });

  useEffect(() => {
    dispatch(getSession());
    dispatch(getProfile());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const account = useAppSelector(state => state.authentication.account);
  const currentLocale = useAppSelector(state => state.locale.currentLocale);
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const isAdmin = useAppSelector(state => hasAnyAuthority(state.authentication.account.authorities, [AUTHORITIES.ADMIN]));
  const isInProduction = useAppSelector(state => state.applicationProfile.inProduction);
  const isOpenAPIEnabled = useAppSelector(state => state.applicationProfile.isOpenAPIEnabled);

  return (
    <Box
      id="--servicelane-app-container"
      sx={{
        m: 0,
        p: 0,
        height: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'top',
      }}
    >
      <ToastContainer position={toast.POSITION.TOP_LEFT} className="toastify-container" toastClassName="toastify-toast" />
      <ErrorBoundary>
        <AppBar
          ref={observe}
          account={account}
          isAuthenticated={isAuthenticated}
          isAdmin={isAdmin}
          currentLocale={currentLocale}
          isInProduction={isInProduction}
          isOpenAPIEnabled={isOpenAPIEnabled}
        />
      </ErrorBoundary>

      <Box
        id="--servicelane-main-content-container"
        component="main"
        sx={{
          height: `calc(100% - ${height}px)`,
          minHeight: `calc(100% - ${height}px)`,
          width: '100%',
          minWidth: '100%',
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
        }}
      >
        <FullPaper id="--servicelane-main-backdrop">
          <Paper
            id="--servicelane-main-paper"
            elevation={3}
            sx={{
              p: 1,
              display: 'flex',
              flexDirection: 'column',
              flexGrow: 1,
              alignItems: 'center',
              backgroundColor: '#303030',
            }}
          >
            <ErrorBoundary>
              <AppRoutes />
            </ErrorBoundary>
          </Paper>
        </FullPaper>
      </Box>
    </Box>
  );
};

export default App;
