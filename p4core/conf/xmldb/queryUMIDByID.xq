xquery version "1.0";
declare namespace mets="http://www.loc.gov/METS/";
declare namespace xlink="http://www.w3.org/1999/xlink";

declare variable $AIPID as xs:string external;
declare variable $COLLECTION as xs:string external;

for $mets in collection($COLLECTION)//mets:mets[@OBJID=$AIPID]

let $flocat := $mets/mets:fileSec/mets:fileGrp/mets:file[@MIMETYPE='application/mxf']/mets:FLocat[@LOCTYPE='URN' and contains(@ID,"DRACMA")]

return data($flocat/@xlink:href)