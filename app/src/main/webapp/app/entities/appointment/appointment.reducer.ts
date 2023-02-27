import axios from 'axios';
import { createAsyncThunk, isFulfilled, isPending, isRejected } from '@reduxjs/toolkit';
import { cleanEntity } from 'app/shared/util/entity-utils';
import { IQueryParams, createEntitySlice, EntityState, serializeAxiosError } from 'app/shared/reducers/reducer.utils';
import { IAppointment, defaultValue } from 'app/shared/model/appointment.model';

const initialState: EntityState<IAppointment> = {
  loading: false,
  errorMessage: null,
  entities: [],
  entity: defaultValue,
  entityById: defaultValue,
  updating: false,
  updateSuccess: false,
};

const apiUrl = 'api/appointments';
const apiSearchUrl = 'api/_search/appointments';

// Actions

export const searchEntities = createAsyncThunk('appointment/search_entity', async ({ query, page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiSearchUrl}?query=${query}`;
  return axios.get<IAppointment[]>(requestUrl);
});

export const getEntities = createAsyncThunk('appointment/fetch_entity_list', async ({ page, size, sort }: IQueryParams) => {
  const requestUrl = `${apiUrl}?cacheBuster=${new Date().getTime()}`;
  return axios.get<IAppointment[]>(requestUrl);
});

export const getEntity = createAsyncThunk(
  'appointment/fetch_entity',
  async (id: string | number) => {
    const requestUrl = `${apiUrl}/${id}`;
    return axios.get<IAppointment>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const getEntityForUser = createAsyncThunk(
  'appointment/fetch_entity_for_user',
  async () => {
    const requestUrl = `${apiUrl}/user`;
    return axios.get<IAppointment>(requestUrl);
  },
  { serializeError: serializeAxiosError }
);

export const createEntity = createAsyncThunk(
  'appointment/create_entity',
  async (entity: IAppointment, thunkAPI) => {
    const result = await axios.post<IAppointment>(apiUrl, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const updateEntity = createAsyncThunk(
  'appointment/update_entity',
  async (entity: IAppointment, thunkAPI) => {
    const result = await axios.put<IAppointment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const partialUpdateEntity = createAsyncThunk(
  'appointment/partial_update_entity',
  async (entity: IAppointment, thunkAPI) => {
    const result = await axios.patch<IAppointment>(`${apiUrl}/${entity.id}`, cleanEntity(entity));
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

export const deleteEntity = createAsyncThunk(
  'appointment/delete_entity',
  async (id: string | number, thunkAPI) => {
    const requestUrl = `${apiUrl}/${id}`;
    const result = await axios.delete<IAppointment>(requestUrl);
    thunkAPI.dispatch(getEntities({}));
    return result;
  },
  { serializeError: serializeAxiosError }
);

// slice

export const AppointmentSlice = createEntitySlice({
  name: 'appointment',
  initialState,
  extraReducers(builder) {
    builder
      .addCase(getEntity.fulfilled, (state, action) => {
        state.loading = false;
        state.entityById = action.payload.data;
      })
      .addCase(getEntityForUser.fulfilled, (state, action) => {
        state.loading = false;
        state.entity = action.payload.data;
      })
      .addCase(deleteEntity.fulfilled, state => {
        state.updating = false;
        state.updateSuccess = true;
        state.entity = {};
      })
      .addMatcher(isFulfilled(getEntities, searchEntities), (state, action) => {
        const { data } = action.payload;

        return {
          ...state,
          loading: false,
          entities: data,
        };
      })
      .addMatcher(isFulfilled(createEntity, updateEntity, partialUpdateEntity), (state, action) => {
        state.updating = false;
        state.loading = false;
        state.updateSuccess = true;
        state.entity = action.payload.data;
      })
      .addMatcher(isPending(getEntities, getEntity, searchEntities), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.loading = true;
      })
      .addMatcher(isPending(createEntity, updateEntity, partialUpdateEntity, deleteEntity), state => {
        state.errorMessage = null;
        state.updateSuccess = false;
        state.updating = true;
      });
  },
});

export const { reset } = AppointmentSlice.actions;

// Reducer
export default AppointmentSlice.reducer;
