import React, { useEffect, useState, useCallback, useMemo } from 'react';
import { REDIRECT_URL } from 'app/shared/util/url-utils';
import {useAppDispatch, useAppSelector} from 'app/config/store';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Typography from '@mui/material/Typography';
import { useForm } from 'react-hook-form';
import { yupResolver } from '@hookform/resolvers/yup';
import * as yup from 'yup';
import { dayjsLocalizer } from 'react-big-calendar';
import dayjs from 'dayjs';
import { FormContainer, TextFieldElement, useFormContext, FormProvider } from 'react-hook-form-mui';
import Stack from '@mui/material/Stack';
import Divider from '@mui/material/Divider';
import { Paper } from '@mui/material';
import {getFullAddress, getFullName} from "app/shared/util/account-utils";
import {createEntity, getEntity, getEntityForUser, reset, updateEntity} from "app/entities/vehicle/vehicle.reducer";
import {getUsers} from "app/shared/reducers/user-management";
import { Link, useNavigate, useParams } from 'react-router-dom';
import _ from "lodash";

const schema = yup
  .object({
    name: yup.string(),
    phone: yup.string(),
    address: yup.string(),
    make: yup.string(),
    model: yup.string(),
    color: yup.string(),
    vin: yup.string(),
  })
  .required();
type FormData = yup.InferType<typeof schema>;

const localizer = dayjsLocalizer(dayjs);

function AccountForm() {
  const methods = useFormContext();
  const dispatch = useAppDispatch();
  const [isNew, setIsNew] = useState<boolean>(false);
  const account = useAppSelector(state => state.authentication.account);
  const vehicleEntity = useAppSelector(state => state.vehicle.entity);
  const loading = useAppSelector(state => state.vehicle.loading);
  const updating = useAppSelector(state => state.vehicle.updating);
  const updateSuccess = useAppSelector(state => state.vehicle.updateSuccess);
  const users = useAppSelector(state => state.userManagement.users);
  const navigate = useNavigate();

  useEffect(() => {
    dispatch(getUsers({}));
    dispatch(getEntityForUser());
  }, []);

  useEffect(() => {
    console.log("setting vehicle entity to.")
    console.log(vehicleEntity)
    if (!_.isEmpty(vehicleEntity)) {
      methods.setValue("make", vehicleEntity.make);
      methods.setValue("model", vehicleEntity.model);
      methods.setValue("color", vehicleEntity.color);
      methods.setValue("vin", vehicleEntity.vin);
      setIsNew(false)
    } else {
      console.log("SETTING AS NEW")
      setIsNew(true)
    }
  }, [vehicleEntity]);

  console.log(users)
  console.log(account)

  const handleUpdateProfile = (data: FormData) => {
    const entity = {
      ...vehicleEntity,
      ...data,
      owner: users.find(it => it.id.toString() === account.id.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  }

  return (
    <Box
      id="--servicelane-account-box"
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        width: '100%',
        flexGrow: 1,
        justifyContent: 'center',
        px: 6,
        py: 1,
      }}
    >
      <FormContainer
        formContext={methods}
        onSuccess={handleUpdateProfile}
        FormProps={{ id: '--servicelane-account-form-container', style: { width: '100%', height: '100%', padding: '0px 40px' } }}
      >
        <Box
          id="--servicelane-account-container"
          sx={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            flexDirection: 'column',
            minHeight: '100%',
          }}
        >
          <Paper
            sx={{
              display: 'flex',
              justifyContent: 'top',
              alignItems: 'center',
              flexDirection: 'column',
              p: 1,
              minHeight: '100%',
              minWidth: '50%',
            }}
          >
            <Stack direction="column" divider={<Divider orientation="horizontal" flexItem />} spacing={1}>
              <Typography color="#303030" align="center" variant="h5" sx={{ mt: 1 }}>
                My Account
              </Typography>
              <TextFieldElement name="name" label="Name" disabled/>
              <TextFieldElement name="address" label="Address" disabled/>
              <TextFieldElement name="phone" label="Phone" disabled/>
              <TextFieldElement name="make" label="Make"/>
              <TextFieldElement name="model" label="Model"/>
              <TextFieldElement name="color" label="Color"/>
              <TextFieldElement name="vin" label="VIN"/>
              <Button variant='outlined' type="submit" >
                Update Profile
              </Button>
            </Stack>
          </Paper>
        </Box>

      </FormContainer>
    </Box>
  );
}

export default function Account() {
  const account = useAppSelector(state => state.authentication.account);
  const methods = useForm<FormData>({
    defaultValues: {
      name: getFullName(account),
      address: getFullAddress(account),
      phone: account.phone ?? '',
    },
    resolver: yupResolver(schema),
  });

  useEffect(() => {
    const redirectURL = localStorage.getItem(REDIRECT_URL);
    if (redirectURL) {
      localStorage.removeItem(REDIRECT_URL);
      //location.href = `${location.origin}${redirectURL}`;
    }
  });

  return  (
      <FormProvider {...methods} >
        <AccountForm />
      </FormProvider>
  )
}
