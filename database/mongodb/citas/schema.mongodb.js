db = db.getSiblingDB('clinica_citas_db');

if (!db.getCollectionNames().includes('citas')) {
  db.createCollection('citas');
}

db.runCommand({
  collMod: 'citas',
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: [
        'pacienteId',
        'medicoId',
        'fecha',
        'horaInicio',
        'horaFin',
        'estado'
      ],
      properties: {
        pacienteId: { bsonType: ['long', 'int'] },
        medicoId: { bsonType: ['long', 'int'] },
        especialidadId: { bsonType: ['long', 'int', 'null'] },
        fecha: {
          bsonType: 'string',
          pattern: '^[0-9]{4}-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01])$'
        },
        horaInicio: {
          bsonType: 'string',
          pattern: '^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$'
        },
        horaFin: {
          bsonType: 'string',
          pattern: '^([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$'
        },
        consultorio: { bsonType: ['string', 'null'] },
        motivo: { bsonType: ['string', 'null'] },
        estado: { enum: ['PROGRAMADA', 'CANCELADA', 'ATENDIDA'] },
        motivoCancelacion: { bsonType: ['string', 'null'] },
        createdAt: { bsonType: ['date', 'null'] },
        updatedAt: { bsonType: ['date', 'null'] }
      }
    }
  },
  validationLevel: 'moderate'
});

db.citas.createIndex({ medicoId: 1, fecha: 1 }, { name: 'idx_citas_medico_fecha' });
db.citas.createIndex({ pacienteId: 1 }, { name: 'pacienteId' });
db.citas.createIndex({ estado: 1 }, { name: 'estado' });
