declare namespace mets="http://www.loc.gov/METS/";
for $mets in //mets:mets
where $mets[not(. //mets:file[@MIMETYPE='video/x-raw-yuv' and @MIMETYPE='audio/x-raw-int'])]
return data($mets/@OBJID)