import React from 'react';
import { styled } from '@mui/material/styles';
import { Container } from '@mui/material';
import { RootContainerProps } from 'app/shared/styled/RootContainer/RootContainer.types';

const RootContainer = styled((props: RootContainerProps) => {
  return <Container {...props} maxWidth={false} disableGutters={true} sx={{ m: 'auto', p: 0 }} />;
})(({ theme }) => ({
  height: '100%',
  width: '100%',
  minWidth: '100%',
  minHeight: '100%',
  backgroundColor: theme.palette.grey[900],
  // backgroundColor: theme.palette.mode == ThemeMode.Light ? "white" : theme.palette.grey[900],
}));

export default RootContainer;
