import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/shared/reducers/user-management';
import { IVehicle } from 'app/shared/model/vehicle.model';
import { getEntity, updateEntity, createEntity, reset } from './vehicle.reducer';

export const VehicleUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const vehicleEntity = useAppSelector(state => state.vehicle.entity);
  const loading = useAppSelector(state => state.vehicle.loading);
  const updating = useAppSelector(state => state.vehicle.updating);
  const updateSuccess = useAppSelector(state => state.vehicle.updateSuccess);

  const handleClose = () => {
    navigate('/vehicle');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...vehicleEntity,
      ...values,
      owner: users.find(it => it.id.toString() === values.owner.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...vehicleEntity,
          owner: vehicleEntity?.owner?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="appApp.vehicle.home.createOrEditLabel" data-cy="VehicleCreateUpdateHeading">
            <Translate contentKey="appApp.vehicle.home.createOrEditLabel">Create or edit a Vehicle</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="vehicle-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField label={translate('appApp.vehicle.make')} id="vehicle-make" name="make" data-cy="make" type="text" />
              <ValidatedField label={translate('appApp.vehicle.model')} id="vehicle-model" name="model" data-cy="model" type="text" />
              <ValidatedField label={translate('appApp.vehicle.color')} id="vehicle-color" name="color" data-cy="color" type="text" />
              <ValidatedField label={translate('appApp.vehicle.vin')} id="vehicle-vin" name="vin" data-cy="vin" type="text" />
              <ValidatedField id="vehicle-owner" name="owner" data-cy="owner" label={translate('appApp.vehicle.owner')} type="select">
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/vehicle" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default VehicleUpdate;
