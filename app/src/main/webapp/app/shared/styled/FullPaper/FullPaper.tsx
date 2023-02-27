import React from 'react';
import { Paper } from '@mui/material';
import { styled } from '@mui/material/styles';
import { FullPaperProps } from 'app/shared/styled/FullPaper/FullPaper.types';

const FullPaper = styled((props: FullPaperProps) => {
  return <Paper variant="outlined" square {...props} />;
})(({ theme }) => ({
  padding: 5,
  display: 'flex',
  flexDirection: 'column',
  height: '100%',
  minHeight: '100%',
  width: '100%',
  minWidth: '100%',
  flexGrow: 1,
  backgroundColor: theme.palette.grey[900],
}));

export default FullPaper;
