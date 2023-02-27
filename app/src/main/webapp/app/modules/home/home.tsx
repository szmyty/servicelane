import React, { useEffect } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import { useAppSelector } from 'app/config/store';
import { Typography } from '@mui/material';
import Button from '@mui/material/Button';
import { Image } from 'mui-image';
import { Link } from 'react-router-dom';
import { RoutePath } from 'app/components/Routes/RoutePath';
import _ from 'lodash';

export default function Home() {
  const account = useAppSelector(state => state.authentication.account);
  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      //location.href = `${location.origin}${redirectURL}`;
    }
  });

  return (
    <>
      <Typography color="common.white" align="center" variant="h5" sx={{ mb: 4, mt: { sx: 4, sm: 10 } }}>
        Hi {_.capitalize(account?.firstName ?? account?.login ?? 'Anonymous')}, Welcome to ServiceLane!
      </Typography>
      <Image src="content/images/servicelane_logo.png" duration={1000} fit="contain" height={200} width={200} />
      <Button
        color="primary"
        variant="contained"
        size="large"
        component={Link}
        to={RoutePath.Reservation}
        sx={{ mt: { sx: 4, sm: 4 }, minWidth: 100 }}
      >
        Book Now
      </Button>
    </>
  );
}
