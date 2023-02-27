import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './appointment.reducer';

export const AppointmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const appointmentEntity = useAppSelector(state => state.appointment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="appointmentDetailsHeading">
          <Translate contentKey="appApp.appointment.detail.title">Appointment</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{appointmentEntity.id}</dd>
          <dt>
            <span id="created">
              <Translate contentKey="appApp.appointment.created">Created</Translate>
            </span>
          </dt>
          <dd>
            {appointmentEntity.created ? <TextFormat value={appointmentEntity.created} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="startTime">
              <Translate contentKey="appApp.appointment.startTime">Start Time</Translate>
            </span>
          </dt>
          <dd>
            {appointmentEntity.startTime ? <TextFormat value={appointmentEntity.startTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="endTime">
              <Translate contentKey="appApp.appointment.endTime">End Time</Translate>
            </span>
          </dt>
          <dd>
            {appointmentEntity.endTime ? <TextFormat value={appointmentEntity.endTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="services">
              <Translate contentKey="appApp.appointment.services">Services</Translate>
            </span>
          </dt>
          <dd>{appointmentEntity.services}</dd>
          <dt>
            <Translate contentKey="appApp.appointment.client">Client</Translate>
          </dt>
          <dd>{appointmentEntity.client ? appointmentEntity.client.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/appointment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/appointment/${appointmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AppointmentDetail;
