# Project report

Generated deliverables:

- `entregable/261.Informe-Proyecto-Microservicios-DSW.docx`
- `entregable/261.Informe-Proyecto-Microservicios-DSW.pdf`
- `entregable/Guia-Tecnica-y-Guion-Video-Microservicios.docx`
- `evidence/01-arquitectura-microservicios.png`
- `evidence/02-esquemas-bases-datos.png`
- `evidence/03-validacion-local.png`

To regenerate the images and documents:

```powershell
python -m pip install -r docs\requirements.txt
python docs\generate_report.py
python docs\generate_video_guide.py
```

Before the final delivery, update the evidence screenshots in `evidence/` if
necessary, then run the generator again.
