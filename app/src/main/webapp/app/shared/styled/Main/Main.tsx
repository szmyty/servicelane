import React from 'react';
import { styled } from '@mui/material/styles';
import Box from '@mui/material/Box';
import { MainProps } from 'app/shared/styled/Main/Main.types';

const Main = styled((props: MainProps) => {
  return <Box component="main" {...props} />;
})(({ theme }) => ({
  margin: 0,
  padding: 0,
  flexDirection: 'column',
  display: 'flex',
  flex: '1 1 auto',
  flexGrow: 1,
  height: '100%',
  width: '100%',
  minWidth: '100%',
  minHeight: '100%',
  overflow: 'hidden',
}));

export default Main;
