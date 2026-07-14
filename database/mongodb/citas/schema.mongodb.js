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
        fecha: { bsonType: ['date', 'string'] },
        horaInicio: { bsonType: ['date', 'string'] },
        horaFin: { bsonType: ['date', 'string'] },
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
