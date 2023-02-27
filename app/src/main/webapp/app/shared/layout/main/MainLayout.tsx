import React from 'react';
import Container from '@mui/material/Container';
import MainRoot from 'app/shared/layout/main/MainRoot';
import Box from '@mui/material/Box';

export default function MainLayout(props: React.HTMLAttributes<HTMLDivElement>) {
  const { children } = props;

  return (
    // <MainRoot>
    <Container
      id="--servicelane-main-layout-container"
      sx={{
        mt: 3,
        mb: 3,
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
      }}
    >
      {children}
      <Box
        sx={{
          position: 'absolute',
          left: 0,
          right: 0,
          top: 0,
          bottom: 0,
          backgroundColor: 'common.black',
          opacity: 0.5,
          zIndex: -1,
        }}
      />
    </Container>
    // </MainRoot>
  );
}
