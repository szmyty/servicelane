import React from 'react';
import { useNavigate } from 'react-router-dom';
import { RoutePath } from 'app/components/Routes/RoutePath';

export const useNavigateTo = () => {
  const navigate = useNavigate();

  const navigateTo = (to: RoutePath | string | undefined) => (e: React.SyntheticEvent) => {
    if (to != null) {
      e.preventDefault();
      navigate(to);
    }
  };

  return { navigateTo };
};
