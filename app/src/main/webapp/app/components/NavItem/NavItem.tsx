import React from 'react';
import { NavLink } from 'react-router-dom';
import Grid from '@mui/material/Grid';
import Button from '@mui/material/Button';
import { NavItemProps } from 'app/components/NavItem/NavItem.types';
import { useRouteActive } from 'app/shared/hooks/useLocationChange';
import { useTheme } from '@mui/material';

export default function NavItem({ text, route }: NavItemProps) {
  const theme = useTheme();
  const isRouteActive = useRouteActive(route);

  const textColor = isRouteActive ? 'white' : 'black';
  const backgroundColor = isRouteActive ? theme.palette.grey[900] : 'primary';

  return (
    <Grid item>
      <Button
        component={NavLink}
        to={route}
        key={text}
        sx={{ my: 2, color: textColor, textAlign: 'center', backgroundColor: { backgroundColor }, display: 'block' }}
      >
        {text}
      </Button>
    </Grid>
  );
}
