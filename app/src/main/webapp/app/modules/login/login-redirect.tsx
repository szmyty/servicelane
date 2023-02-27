import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useLocation } from 'react-router-dom';

export const LoginRedirect = () => {
  const location = useLocation();

  useEffect(() => {
    console.log('Redirecting')
    console.log(location)
    // @ts-ignore
    localStorage.setItem(REDIRECT_URL, location.state.from.pathname);
    window.location.reload();
  });

  return null;
};

export default LoginRedirect;
