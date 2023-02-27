import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Provider } from 'react-redux';
import { bindActionCreators } from 'redux';
import { Helmet, HelmetProvider } from 'react-helmet-async';
import getStore from 'app/config/store';
import { registerLocale } from 'app/config/translation';
import setupAxiosInterceptors from 'app/config/axios-interceptor';
import { clearAuthentication } from 'app/shared/reducers/authentication';
import ErrorBoundary from 'app/shared/error/error-boundary';
import App from 'app/app';
import { loadIcons } from 'app/config/icon-loader';
import { StyledEngineProvider } from '@mui/material/styles';
import { FontProvider } from 'app/shared/context/FontContext';
import { fonts } from 'app/shared/fonts';
import CssBaseline from '@mui/material/CssBaseline/CssBaseline';
import { ThemeProvider } from 'app/shared/context/ThemeContext';

const store = getStore();
registerLocale(store);

const actions = bindActionCreators({ clearAuthentication }, store.dispatch);
setupAxiosInterceptors(() => actions.clearAuthentication('login.error.unauthorized'));

loadIcons();

const baseHref = document.querySelector('base').getAttribute('href').replace(/\/$/, '');
const container = document.getElementById('root');
const root = createRoot(container);

root.render(
  <React.StrictMode>
    <ErrorBoundary>
      <StyledEngineProvider injectFirst>
        <CssBaseline />
        <Provider store={store}>
          <FontProvider fonts={fonts}>
            <ThemeProvider>
              <BrowserRouter basename={baseHref}>
                <HelmetProvider>
                  <Helmet>{/* Add to header here. */}</Helmet>
                </HelmetProvider>
                <App />
              </BrowserRouter>
            </ThemeProvider>
          </FontProvider>
        </Provider>
      </StyledEngineProvider>
    </ErrorBoundary>
  </React.StrictMode>
);
